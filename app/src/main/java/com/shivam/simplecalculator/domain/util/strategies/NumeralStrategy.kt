package com.shivam.simplecalculator.domain.util.strategies

import com.shivam.simplecalculator.domain.models.UnitOption

class NumeralStrategy : ConverterStrategy {
    override fun convert(value1: Double, value2: Double, from: UnitOption, to: UnitOption): Double {
        val baseFrom = from.factor.toInt()
        val baseTo = to.factor.toInt()
        
        if (baseFrom == baseTo) return value1
        
        try {
            val fromStr = value1.toLong().toString()
            val decValue = java.lang.Long.parseLong(fromStr, baseFrom)
            val toStr = java.lang.Long.toString(decValue, baseTo)
            return toStr.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            return 0.0
        }
    }

    fun convertBase(value: String, fromBase: Int, toBase: Int): String {
        if (value.isEmpty() || value == ".") return ""
        if (fromBase == toBase) return value

        try {
            val parts = value.split(".")
            val intPartStr = parts[0].ifEmpty { "0" }
            val fracPartStr = if (parts.size > 1) parts[1] else ""

            var decIntPart = 0L
            if (fromBase != 10) {
                decIntPart = java.lang.Long.parseLong(intPartStr, fromBase)
            } else {
                decIntPart = intPartStr.toLong()
            }

            var decFracPart = 0.0
            if (fracPartStr.isNotEmpty()) {
                if (fromBase != 10) {
                    var fractionValue = 0.0
                    for (i in fracPartStr.indices) {
                        val digit = Character.digit(fracPartStr[i], fromBase)
                        if (digit < 0) return "Invalid input"
                        fractionValue += digit / Math.pow(fromBase.toDouble(), (i + 1).toDouble())
                    }
                    decFracPart = fractionValue
                } else {
                    decFracPart = ("0." + fracPartStr).toDouble()
                }
            }

            var resultStr = ""
            if (toBase != 10) {
                resultStr = java.lang.Long.toString(decIntPart, toBase).uppercase()
            } else {
                resultStr = decIntPart.toString()
            }

            if (decFracPart > 0) {
                resultStr += "."
                var fraction = decFracPart
                val maxFractionDigits = 8
                for (i in 0 until maxFractionDigits) {
                    fraction *= toBase
                    val digit = fraction.toInt()
                    resultStr += Character.forDigit(digit, toBase).uppercase()
                    fraction -= digit
                    if (fraction == 0.0) break
                }
            } else if (parts.size > 1 && parts[1].isEmpty()) {
                resultStr += "."
            }

            return resultStr
        } catch (e: Exception) {
            return "Invalid input"
        }
    }
}
