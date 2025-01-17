package com.example.hydrohero.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class WaterIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: LocalDate,
    val amount: Int
)