package com.shivam.simplecalculator.strategies

import com.shivam.simplecalculator.models.UnitOption

class BmiStrategy : ConverterStrategy {
    override fun convert(value1: Double, value2: Double, from: UnitOption, to: UnitOption): Double {
        // value1 = weight, value2 = height
        // BMI = weight(kg) / (height(m) * height(m))
        val weightInKg = if (from.name == "Pounds") value1 * 0.453592 else value1
        val heightInM = when (to.name) {
            "Centimeters" -> value2 / 100.0
            "Inches" -> value2 * 0.0254
            else -> value2 / 100.0 // Default to Centimeters just in case
        }
        
        if (heightInM <= 0.0) return 0.0
        return weightInKg / (heightInM * heightInM)
    }
}
