package com.example.hydrohero.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import com.example.hydrohero.database.WaterIntake
import com.example.hydrohero.database.WaterIntakeDao
import kotlinx.coroutines.flow.*
import java.time.temporal.ChronoUnit

class MainViewModelFactory(private val waterIntakeDao: WaterIntakeDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(waterIntakeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class MainViewModel(private val waterIntakeDao: WaterIntakeDao) : ViewModel() {
    private val _uiState = MutableStateFlow(HydroHeroState())
    val uiState: StateFlow<HydroHeroState> = _uiState

    init {
        viewModelScope.launch {
            waterIntakeDao.getIntakeForDate(LocalDate.now()).collect { intake ->
                _uiState.value = _uiState.value.copy(
                    currentIntake = intake?.amount ?: 0
                )
                updateProgress()
            }
        }
        loadHistory()
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
            }.collect()
        }
    }

    fun addWater(amount: Int) {
        viewModelScope.launch {
            val currentDate = LocalDate.now()
            val newTotal = _uiState.value.currentIntake + amount
            waterIntakeDao.insertOrUpdate(WaterIntake(date = currentDate, amount = newTotal))
        }
    }

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(dailyGoal = goal)
            updateProgress()
        }
    }

    private fun updateProgress() {
        val progress = (_uiState.value.currentIntake.toFloat() / _uiState.value.dailyGoal) * 100
        _uiState.value = _uiState.value.copy(progress = progress)
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

    private fun scheduleReminders() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(2 * 60 * 60 * 1000)
                sendReminder()
            }
        }
    }

    private fun sendReminder() {
        println("Don't forget to drink water!")
    }

    private fun checkAchievements() {
        val currentAchievements = _uiState.value.achievements.toMutableList()

        if (_uiState.value.currentIntake >= _uiState.value.dailyGoal && !currentAchievements.contains("Daily Goal Achieved")) {
            currentAchievements.add("Daily Goal Achieved")
        }

        if (_uiState.value.intakeHistory.size >= 7 && !currentAchievements.contains("Week Streak")) {
            currentAchievements.add("Week Streak")
        }

        _uiState.value = _uiState.value.copy(achievements = currentAchievements)
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
)

data class SocialPost(
    val timestamp: LocalDateTime,
    val content: String
)