package com.example.hydrohero.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var database: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "hydrohero_database"
            ).build()
            database = instance
            instance
        }
    }

    fun provideWaterIntakeDao(context: Context): WaterIntakeDao {
        return provideDatabase(context).waterIntakeDao()
    }
}