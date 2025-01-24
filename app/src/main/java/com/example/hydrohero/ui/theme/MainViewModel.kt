package com.example.hydrohero.ui.theme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.Manifest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import com.example.hydrohero.database.WaterIntake
import com.example.hydrohero.database.WaterIntakeDao
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import java.time.temporal.ChronoUnit
import com.example.hydrohero.R

class MainViewModelFactory(
    private val waterIntakeDao: WaterIntakeDao,
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(waterIntakeDao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class MainViewModel(
    private val waterIntakeDao: WaterIntakeDao,
    private val context: Context
) : ViewModel() {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val prefs = context.getSharedPreferences("HydroHeroPrefs", Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow(HydroHeroState(
        dailyGoal = 2000,
    ))
    val uiState: StateFlow<HydroHeroState> = _uiState

    init {
        createNotificationChannel()
        scheduleReminders()
        val savedProfileJson = prefs.getString("PROFILE_JSON", null)
        val savedProfile = savedProfileJson?.let { Gson().fromJson(it, UserProfile::class.java) }
        _uiState.value = _uiState.value.copy(userProfile = savedProfile ?: UserProfile())
        val savedGoal = prefs.getInt("DAILY_GOAL", 2000)
        _uiState.value = _uiState.value.copy(dailyGoal = savedGoal)

        viewModelScope.launch {
            waterIntakeDao.getIntakeForDate(LocalDate.now())
                .distinctUntilChanged()
                .collect { intake ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            currentIntake = intake?.amount ?: 0
                        )
                    }
                    updateProgress()
                    checkAchievements()
                }
        }
        loadHistory()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "HYDRO_HERO_CHANNEL",
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Water intake reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendWaterReminderNotification(message: String) {
        val intent = Intent(context, MainViewModel::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "HYDRO_HERO_CHANNEL")
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle("Hydration Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(1, builder.build())
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val startOfWeek = today.minusDays(6)
            val startOfMonth = today.withDayOfMonth(1)

            combine(
                waterIntakeDao.getIntakesBetweenDates(startOfWeek, today),
                waterIntakeDao.getIntakesBetweenDates(startOfMonth, today.minusDays(7))
            ) { weeklyIntakes: List<WaterIntake>, monthlyIntakes: List<WaterIntake> ->
                _uiState.value = _uiState.value.copy(
                    weeklyIntakes = weeklyIntakes.associateBy { it.date },
                    monthlyIntakes = monthlyIntakes.groupBy { it.date.withDayOfMonth(1) }
                        .mapValues { (_, intakes) -> intakes.sumOf { it.amount } }
                )
                checkAchievements()
            }.collect()
        }
    }

    fun addWater(amount: Int) {
        viewModelScope.launch {
            val currentDate = LocalDate.now()
            val newTotal = _uiState.value.currentIntake + amount
            _uiState.update { currentState ->
                currentState.copy(currentIntake = newTotal)
            }
            waterIntakeDao.insertOrUpdate(WaterIntake(date = currentDate, amount = newTotal))

            // Update last intake timestamp
            prefs.edit().putLong("LAST_INTAKE_TIMESTAMP", System.currentTimeMillis()).apply()

            updateProgress()
            checkAchievements()
        }
    }

    private fun getLastIntakeTime(): Long {
        return prefs.getLong("LAST_INTAKE_TIMESTAMP", System.currentTimeMillis())
    }

    private fun scheduleReminders() {
        viewModelScope.launch {
            while (true) {
                val currentTime = System.currentTimeMillis()
                val lastIntakeTime = getLastIntakeTime()
                val timeSinceLastIntake = currentTime - lastIntakeTime

                if (timeSinceLastIntake > 3 * 60 * 60 * 1000 &&
                    _uiState.value.currentIntake < _uiState.value.dailyGoal) {

                    val remainingWater = _uiState.value.dailyGoal - _uiState.value.currentIntake
                    val reminderMessage = "Don't forget to drink water! You still need $remainingWater ml to reach your daily goal."

                    sendWaterReminderNotification(reminderMessage)
                }

                kotlinx.coroutines.delay(60 * 60 * 1000)
            }
        }
    }

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(dailyGoal = goal)
            }
            prefs.edit().putInt("DAILY_GOAL", goal).apply()
            updateProgress()
            checkAchievements()
        }
    }

    private fun updateProgress() {
        println("Updating progress")
        val progress = (_uiState.value.currentIntake.toFloat() / _uiState.value.dailyGoal) * 100
        println("Progress calculation: ${_uiState.value.currentIntake} / ${_uiState.value.dailyGoal} = $progress")
        _uiState.update { currentState ->
            currentState.copy(progress = progress)
        }
    }

    fun logDailyIntake() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val currentHistory = _uiState.value.intakeHistory.toMutableMap()
            currentHistory[today] = _uiState.value.currentIntake
            _uiState.value = _uiState.value.copy(
                intakeHistory = currentHistory,
                currentIntake = 0
            )
            updateProgress()
        }
    }


    private fun checkAchievements() {
        val currentAchievements = _uiState.value.achievements.toMutableList()

        // Achievement: Daily Goal Achieved
        if (_uiState.value.currentIntake >= _uiState.value.dailyGoal && !currentAchievements.contains("Daily Goal Achieved")) {
            currentAchievements.add("Daily Goal Achieved")
        }

        // Achievement: Week Streak
        if (_uiState.value.intakeHistory.size >= 7 && !currentAchievements.contains("Week Streak")) {
            currentAchievements.add("Week Streak")
        }

        // Achievement: Longest Streak
        val longestStreak = calculateLongestStreak(_uiState.value.intakeHistory)
        if (longestStreak >= 5 && !currentAchievements.contains("Longest Streak")) {
            currentAchievements.add("Longest Streak")
        }

        // Achievement: Total Intake (1000 ml)
        val totalIntake = _uiState.value.intakeHistory.values.sum()
        if (totalIntake >= 1000 && !currentAchievements.contains("1000 Intake")) {
            currentAchievements.add("1000 Intake")
        }

        _uiState.value = _uiState.value.copy(achievements = currentAchievements)
    }

    private fun calculateLongestStreak(intakeHistory: Map<String, Int>): Int {
        var maxStreak = 0
        var currentStreak = 0

        for (intake in intakeHistory.values) {
            if (intake > 0) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 0
            }
        }

        return maxStreak
    }

    fun shareProgress() {
        val message = "I've drunk ${_uiState.value.currentIntake}ml of water today with HydroHero!"
        println("Sharing: $message")
    }

    fun addSocialPost(post: String) {
        viewModelScope.launch {
            val currentFeed = _uiState.value.socialFeed.toMutableList()
            currentFeed.add(0, SocialPost(LocalDateTime.now(), post))
            _uiState.value = _uiState.value.copy(socialFeed = currentFeed)
        }
    }

    fun getPersonalizedRecommendation(): String {
        return when {
            _uiState.value.currentIntake < _uiState.value.dailyGoal * 0.5 -> "You're falling behind! Try to drink more water."
            _uiState.value.currentIntake < _uiState.value.dailyGoal -> "You're doing well, but try to drink a bit more to reach your goal."
            else -> "Great job! You've reached your daily goal."
        }
    }

    fun updateUserProfile(
        weightKg: Double? = null,
        age: Int? = null,
        location: String? = null,
        activityLevel: String? = null,
        gender: String? = null,
        dailyGoal: Int? = null
    ) {
        viewModelScope.launch {
            val updatedProfile = _uiState.value.userProfile.copy(
                weightKg = weightKg ?: _uiState.value.userProfile.weightKg,
                age = age ?: _uiState.value.userProfile.age,
                location = location ?: _uiState.value.userProfile.location,
                activityLevel = activityLevel ?: _uiState.value.userProfile.activityLevel,
                gender = gender?.lowercase() ?: _uiState.value.userProfile.gender,
                dailyGoal = dailyGoal ?: _uiState.value.userProfile.dailyGoal
            )

            saveUserProfileToPrefs(updatedProfile)

            _uiState.value = _uiState.value.copy(userProfile = updatedProfile)

            calculatePersonalizedWaterGoal()
        }
    }

    fun saveUserProfileToPrefs(profile: UserProfile) {
        with(prefs.edit()) {
            putString("PROFILE_JSON", Gson().toJson(profile))
            apply()
        }
    }

    private fun calculatePersonalizedWaterGoal() {
        val profile = _uiState.value.userProfile
        val baselineIntake = calculateBaseline(profile.weightKg)
        val activityAdjustment = calculateActivityAdjustment(profile.activityLevel)

        // Placeholder for weather-based adjustment
        val weatherAdjustment = fetchWeatherAndCalculateAdjustment(profile.location)

        val dailyGoal = (baselineIntake + activityAdjustment + weatherAdjustment).toInt()

        setDailyGoal(dailyGoal)
    }

    private fun fetchWeatherAndCalculateAdjustment(location: String): Int {
        // TODO: Implement actual weather API call
        return when {
            location.contains("desert") -> 500
            location.contains("tropical") -> 400
            location.contains("arctic") -> -100
            else -> 0
        }
    }

    private fun calculateBaseline(weightKg: Double): Double {
        val hydrationFactor = 35.0
        return weightKg * hydrationFactor
    }

    private fun calculateActivityAdjustment(activityLevel: String): Int {
        return when (activityLevel) {
            "sedentary" -> 0
            "moderate" -> 250
            "active" -> 500
            else -> 0
        }
    }
}

data class HydroHeroState(
    val dailyGoal: Int = 2000,
    val currentIntake: Int = 0,
    val progress: Float = 0f,
    val intakeHistory: Map<String, Int> = emptyMap(),
    val achievements: List<String> = emptyList(),
    val socialFeed: List<SocialPost> = emptyList(),
    val weeklyIntakes: Map<LocalDate, WaterIntake> = emptyMap(),
    val monthlyIntakes: Map<LocalDate, Int> = emptyMap(),
    val userProfile: UserProfile = UserProfile()
)

data class SocialPost(
    val timestamp: LocalDateTime,
    val content: String
)

data class UserProfile(
    val weightKg: Double = 70.0,
    val age: Int = 30,
    val gender: String = "other",
    val activityLevel: String = "moderate",
    val location: String = "",
    val climate: String = "moderate",
    val dailyGoal: Int? = 2000
)
