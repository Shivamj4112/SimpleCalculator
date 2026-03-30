package com.shivam.simplecalculator.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shivam.simplecalculator.data.repository.CalculationHistory
import com.shivam.simplecalculator.domain.models.CurrencyModel

@Database(entities = [CalculationHistory::class, CurrencyModel::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calculator_database"
                )
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}