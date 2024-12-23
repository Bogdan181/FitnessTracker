package com.example.fitnesstracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    // Використовуємо suspend функцію для вставки
    @Insert
    suspend fun insert(training: Training): Long  // Повертає Long, який є ідентифікатором запису

    @Query("SELECT * FROM training")
    fun getAllTrainings(): Flow<List<Training>>  // Повертає Flow зі списком Training

    @Query("SELECT SUM(calories) FROM training")
    fun getTotalCalories(): Flow<Int>  // Повертає Flow з сумою калорій
}
