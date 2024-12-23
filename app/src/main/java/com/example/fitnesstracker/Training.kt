package com.example.fitnesstracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training")
data class Training(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val calories: Int,
    val date: String
)



