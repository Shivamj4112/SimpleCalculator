package com.shivam.simplecalculator.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shivam.simplecalculator.domain.models.CurrencyModel

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies")
    suspend fun getAllCurrencies(): List<CurrencyModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<CurrencyModel>)

    @Query("DELETE FROM currencies")
    suspend fun clearAll()
}
