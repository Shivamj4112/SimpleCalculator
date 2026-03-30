package com.shivam.simplecalculator.domain.models

import com.shivam.simplecalculator.domain.util.strategies.BmiStrategy
import com.shivam.simplecalculator.domain.util.strategies.ConverterStrategy
import com.shivam.simplecalculator.domain.util.strategies.DateStrategy
import com.shivam.simplecalculator.domain.util.strategies.DiscountStrategy
import com.shivam.simplecalculator.domain.util.strategies.GeneralUnitStrategy
import com.shivam.simplecalculator.domain.util.strategies.NumeralStrategy
import com.shivam.simplecalculator.domain.util.strategies.TemperatureStrategy

enum class ConverterType {
    BMI, DISCOUNT, LENGTH, MASS, NUMERAL, SPEED, TEMPERATURE, TIME, VOLUME, DATA, DATE
}

data class UnitOption(val name: String, val factor: Double)

object ConverterConfig {
    val unitMap: Map<ConverterType, List<UnitOption>> = mapOf(
        ConverterType.LENGTH to listOf(
            UnitOption("Centimeters", 0.01),
            UnitOption("Feet", 0.3048),
            UnitOption("Inches", 0.0254),
            UnitOption("Kilometers", 1000.0),
            UnitOption("Meters", 1.0),
            UnitOption("Miles", 1609.344),
            UnitOption("Millimeters", 0.001),
            UnitOption("Yards", 0.9144)
        ),
        ConverterType.DATA to listOf(
            UnitOption("Bytes", 1.0),
            UnitOption("GB", 1024.0 * 1024 * 1024),
            UnitOption("KB", 1024.0),
            UnitOption("MB", 1024.0 * 1024),
            UnitOption("TB", 1024.0 * 1024 * 1024 * 1024)
        ),
        ConverterType.MASS to listOf(
            UnitOption("Grams", 0.001),
            UnitOption("Kilograms", 1.0),
            UnitOption("Ounces", 0.0283495),
            UnitOption("Pounds", 0.453592)
        ),
        ConverterType.TIME to listOf(
            UnitOption("Days", 86400.0),
            UnitOption("Hours", 3600.0),
            UnitOption("Minutes", 60.0),
            UnitOption("Seconds", 1.0)
        ),
        ConverterType.VOLUME to listOf(
            UnitOption("Cubic Meters", 1000.0),
            UnitOption("Cups", 0.236588),
            UnitOption("Gallons", 3.78541),
            UnitOption("Liters", 1.0),
            UnitOption("Milliliters", 0.001),
            UnitOption("Teaspoons", 0.00492892)
        ),
        ConverterType.SPEED to listOf(
            UnitOption("km/h", 0.277778),
            UnitOption("m/s", 1.0),
            UnitOption("mph", 0.44704)
        ),
        ConverterType.NUMERAL to listOf(
            UnitOption("Binary", 2.0),
            UnitOption("Decimal", 10.0),
            UnitOption("Hexadecimal", 16.0),
            UnitOption("Octal", 8.0)
        ),
        ConverterType.TEMPERATURE to listOf(
            UnitOption("Celsius", 1.0),
            UnitOption("Fahrenheit", 2.0),
            UnitOption("Kelvin", 3.0)
        ),
        ConverterType.DISCOUNT to listOf(
            UnitOption("Amount", 1.0),
            UnitOption("Percentage", 1.0)
        ),
        ConverterType.BMI to listOf(
            UnitOption("Kilograms", 1.0),
            UnitOption("Pounds", 0.453592),
            UnitOption("Centimeters", 0.01),
            UnitOption("Inches", 0.0254)
        ),
        ConverterType.DATE to listOf(
            UnitOption("Start Date", 1.0),
            UnitOption("End Date", 1.0)
        )
    )

    fun getStrategy(type: ConverterType): ConverterStrategy {
        return when (type) {
            ConverterType.BMI -> BmiStrategy()
            ConverterType.TEMPERATURE -> TemperatureStrategy()
            ConverterType.DISCOUNT -> DiscountStrategy()
            ConverterType.NUMERAL -> NumeralStrategy()
            ConverterType.DATE -> DateStrategy()
            else -> GeneralUnitStrategy()
        }
    }
}
