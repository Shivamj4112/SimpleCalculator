package com.shivam.simplecalculator.domain.util.strategies

import com.shivam.simplecalculator.domain.models.UnitOption

class GeneralUnitStrategy : ConverterStrategy {
    override fun convert(value1: Double, value2: Double, from: UnitOption, to: UnitOption): Double {
        val base = value1 * from.factor
        return base / to.factor
    }
}
