package com.example.fitnesstracker

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Training::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
}

