package com.shivam.simplecalculator.domain.util.strategies

import com.shivam.simplecalculator.domain.models.UnitOption

interface ConverterStrategy {
    fun convert(value1: Double, value2: Double, from: UnitOption, to: UnitOption): Double
}
