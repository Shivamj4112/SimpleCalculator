package com.shivam.simplecalculator.data.api

import com.shivam.simplecalculator.models.CurrencyModel
import com.shivam.simplecalculator.models.CurrencyResponse
import retrofit2.Response
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("calculatorround.php")
    suspend fun getExchangeRates(): Response<CurrencyResponse>
}
