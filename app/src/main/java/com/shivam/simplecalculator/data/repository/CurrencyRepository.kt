package com.shivam.simplecalculator.data.repository

import com.shivam.simplecalculator.data.api.CurrencyApiService
import com.shivam.simplecalculator.data.db.CurrencyDao
import com.shivam.simplecalculator.data.db.Metadata
import com.shivam.simplecalculator.data.db.MetadataDao
import com.shivam.simplecalculator.models.CurrencyModel
import com.shivam.simplecalculator.models.CurrencyResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val apiService: CurrencyApiService,
    private val currencyDao: CurrencyDao,
    private val metadataDao: MetadataDao
) {
    private val KEY_LAST_FETCH = "last_fetch_timestamp"
    private val CACHE_EXPIRATION_MS = 24 * 60 * 60 * 1000L // 24 hours

    suspend fun getCurrencies(): Flow<List<CurrencyModel>> = flow {
        val lastFetch = metadataDao.getValue(KEY_LAST_FETCH) ?: 0L
        val currentTime = System.currentTimeMillis()

        val localData = currencyDao.getAllCurrencies()
        if (localData.isNotEmpty()) {
            emit(localData)
        }

        if (currentTime - lastFetch > CACHE_EXPIRATION_MS || localData.isEmpty()) {
            try {
                val response = apiService.getExchangeRates()
                if (response.isSuccessful && response.body() != null) {
                    val currencies = response.body()!!.data.map {
                        if (it.countryName == "United States") it.copy(countryName = "USA") else it
                    }
                    currencyDao.clearAll()
                    currencyDao.insertAll(currencies)
                    metadataDao.insert(Metadata(KEY_LAST_FETCH, currentTime))
                    emit(currencies)
                }
            } catch (e: Exception) {
                // If offline and no local data, we might want to emit empty or error
                // But current logic emits whatever is in local storage first.
            }
        }
    }
}
