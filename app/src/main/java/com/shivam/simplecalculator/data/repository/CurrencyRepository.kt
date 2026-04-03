package com.shivam.simplecalculator.data.repository

import com.shivam.simplecalculator.data.api.CurrencyApiService
import com.shivam.simplecalculator.data.db.CurrencyDao
import com.shivam.simplecalculator.domain.models.CurrencyModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val apiService: CurrencyApiService,
    private val currencyDao: CurrencyDao
) {

    fun getCurrencies(): Flow<List<CurrencyModel>> = flow {
        val localData = currencyDao.getAllCurrencies()
        if (localData.isNotEmpty()) {
            emit(localData)
        }

        val lastUpdate = com.shivam.simplecalculator.domain.util.SharedPrefHelper.lastCurrencyUpdateDate
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())

        if (lastUpdate != today && com.shivam.simplecalculator.domain.util.NetworkUtils.hasInternetAccess()) {
            try {
                val response = apiService.getExchangeRates()
                if (response.isSuccessful && response.body() != null) {
                    val currencies = response.body()!!.data.map {
                        if (it.countryName == "United States") it.copy(countryName = "USA") else it
                    }
                    currencyDao.clearAll()
                    currencyDao.insertAll(currencies)
                    com.shivam.simplecalculator.domain.util.SharedPrefHelper.lastCurrencyUpdateDate = today
                    emit(currencies)
                }
            } catch (e: Exception) {
            }
        }
    }
}
