package com.shivam.simplecalculator.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyModel(
    @PrimaryKey val currencyCode: String,
    val currencyName: String,
    val rate: Double,
    val currencySign: String? = null,
    val countryName: String? = null,
    val roundIcon: String? = null,
    val squareIcon: String? = null
)
