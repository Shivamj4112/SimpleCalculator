package com.shivam.simplecalculator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class CalculationHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
