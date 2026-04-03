package com.shivam.simplecalculator.data.model

import com.shivam.simplecalculator.domain.models.CurrencyModel

data class CurrencyState(
    val currencies: List<CurrencyModel> = emptyList(),
    val baseCurrency: CurrencyModel? = null,
    val convertedCurrency1: CurrencyModel? = null,
    val convertedCurrency2: CurrencyModel? = null,
    val inputAmount: String = "1",
    val result1: String = "0",
    val result2: String = "0",
    val isLoading: Boolean = false,
    val error: String? = null
)