package com.shivam.simplecalculator.domain.util.strategies

import com.shivam.simplecalculator.domain.models.UnitOption

class TemperatureStrategy : ConverterStrategy {
    override fun convert(value1: Double, value2: Double, from: UnitOption, to: UnitOption): Double {
        val fName = from.name
        val tName = to.name

        if (fName == tName) return value1

        val celsius = when (fName) {
            "Fahrenheit" -> (value1 - 32.0) * 5.0 / 9.0
            "Kelvin" -> value1 - 273.15
            else -> value1 // Celsius
        }

        return when (tName) {
            "Fahrenheit" -> celsius * 9.0 / 5.0 + 32.0
            "Kelvin" -> celsius + 273.15
            else -> celsius // Celsius
        }
    }
}
