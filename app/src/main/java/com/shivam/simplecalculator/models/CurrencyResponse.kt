package com.shivam.simplecalculator.models

data class CurrencyResponse(
    val success: Boolean,
    val message: String,
    val data: List<CurrencyModel>
)
