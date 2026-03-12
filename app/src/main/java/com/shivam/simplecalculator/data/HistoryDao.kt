package com.shivam.simplecalculator.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert
    fun insert(history: CalculationHistory)

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationHistory>>

    @Query("DELETE FROM history_table")
    fun clearHistory()

    @Query("DELETE FROM history_table WHERE id IN (:ids)")
    fun deleteHistoryItems(ids: List<Long>)
}
