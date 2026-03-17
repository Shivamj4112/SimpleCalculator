package com.shivam.simplecalculator.strategies

import com.shivam.simplecalculator.models.UnitOption

class DiscountStrategy : ConverterStrategy {
    override fun convert(value1: Double, value2: Double, from: UnitOption, to: UnitOption): Double {
        // value1 is original price, value2 is discount percentage
        return value1 - (value1 * value2 / 100.0)
    }
}
