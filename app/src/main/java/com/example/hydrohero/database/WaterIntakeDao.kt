package com.example.hydrohero.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WaterIntakeDao {
    @Query("SELECT * FROM WaterIntake WHERE date = :date")
    fun getIntakeForDate(date: LocalDate): Flow<WaterIntake?>

    @Query("SELECT * FROM WaterIntake WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getIntakesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<WaterIntake>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(waterIntake: WaterIntake)
}