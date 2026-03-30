package com.shivam.simplecalculator.domain.models

data class CurrencyResponse(
    val success: Boolean,
    val message: String,
    val data: List<CurrencyModel>
)
