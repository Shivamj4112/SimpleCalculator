package com.shivam.simplecalculator.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getAllHistory(): Flow<List<CalculationHistory>> {
        return historyDao.getAllHistory()
    }

    suspend fun insert(history: CalculationHistory) {
        withContext(Dispatchers.IO) {
            historyDao.insert(history)
        }
    }

    suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            historyDao.clearHistory()
        }
    }

    suspend fun deleteHistoryItems(ids: List<Long>) {
        withContext(Dispatchers.IO) {
            historyDao.deleteHistoryItems(ids)
        }
    }
}
