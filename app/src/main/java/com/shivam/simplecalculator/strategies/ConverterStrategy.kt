package com.shivam.simplecalculator.strategies

import com.shivam.simplecalculator.models.UnitOption

interface ConverterStrategy {
    fun convert(value1: Double, value2: Double, from: UnitOption, to: UnitOption): Double
}
