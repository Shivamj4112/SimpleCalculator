package com.shivam.simplecalculator.strategies

import com.shivam.simplecalculator.models.UnitOption

class NumeralStrategy : ConverterStrategy {
    override fun convert(value1: Double, value2: Double, from: UnitOption, to: UnitOption): Double {
        val baseFrom = from.factor.toInt()
        val baseTo = to.factor.toInt()
        
        if (baseFrom == baseTo) return value1
        
        try {
            // Treat the double value as a string representation of the number in baseFrom
            // E.g., if value1 is 1010.0 (binary), we convert it to "1010" and parse as Long
            val fromStr = value1.toLong().toString()
            val decValue = java.lang.Long.parseLong(fromStr, baseFrom)
            val toStr = java.lang.Long.toString(decValue, baseTo)
            return toStr.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            return 0.0
        }
    }
}
