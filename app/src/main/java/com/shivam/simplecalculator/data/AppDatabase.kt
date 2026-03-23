package com.shivam.simplecalculator.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.shivam.simplecalculator.models.CurrencyModel
import com.shivam.simplecalculator.data.db.CurrencyDao
import com.shivam.simplecalculator.data.db.Metadata
import com.shivam.simplecalculator.data.db.MetadataDao

@Database(entities = [CalculationHistory::class, CurrencyModel::class, Metadata::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun metadataDao(): MetadataDao

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
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
