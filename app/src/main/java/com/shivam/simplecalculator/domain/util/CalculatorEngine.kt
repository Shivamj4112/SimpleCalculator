package com.shivam.simplecalculator.domain.util

import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import kotlin.math.*

object CalculatorEngine {

    fun evaluate(expression: String, isDeg: Boolean): Result<Double> {
        var sanitized = expression.trim()
        if (sanitized.isEmpty()) return Result.success(0.0)

        val readyToValidate = ExpressionFormatter.preprocessForEvaluation(sanitized)
        
        if (!ExpressionValidator.isValid(readyToValidate)) {
            return Result.failure(Exception("Invalid Expression"))
        }

        // Handle percentages: convert 50% to (50/100)
        val finalExpression = readyToValidate.replace(Regex("([\\d.]+)(%+)")) { matchResult ->
            val numberStr = matchResult.groupValues[1]
            val percentString = matchResult.groupValues[2]
            var number = numberStr.toDoubleOrNull() ?: 0.0
            for (i in 1..percentString.length) {
                number /= 100
            }
            number.toString()
        }

        val factFunc = object : Function("fact", 1) {
            override fun apply(vararg args: Double): Double {
                val n = args[0]
                if (n < 0 || n != n.toInt().toDouble()) throw IllegalArgumentException("Invalid factorial")
                var res = 1.0
                for (i in 2..n.toInt()) res *= i
                return res
            }
        }

        return try {
            val sinFunc = object : Function("sin", 1) {
                override fun apply(vararg args: Double): Double {
                    return sin(if (isDeg) Math.toRadians(args[0]) else args[0])
                }
            }
            val cosFunc = object : Function("cos", 1) {
                override fun apply(vararg args: Double): Double {
                    return cos(if (isDeg) Math.toRadians(args[0]) else args[0])
                }
            }
            val tanFunc = object : Function("tan", 1) {
                override fun apply(vararg args: Double): Double {
                    return tan(if (isDeg) Math.toRadians(args[0]) else args[0])
                }
            }
            val asinFunc = object : Function("asin", 1) {
                override fun apply(vararg args: Double): Double {
                    val res = asin(args[0])
                    return if (isDeg) Math.toDegrees(res) else res
                }
            }
            val acosFunc = object : Function("acos", 1) {
                override fun apply(vararg args: Double): Double {
                    val res = acos(args[0])
                    return if (isDeg) Math.toDegrees(res) else res
                }
            }
            val atanFunc = object : Function("atan", 1) {
                override fun apply(vararg args: Double): Double {
                    val res = atan(args[0])
                    return if (isDeg) Math.toDegrees(res) else res
                }
            }
            val lnFunc = object : Function("ln", 1) {
                override fun apply(vararg args: Double): Double {
                    return ln(args[0])
                }
            }
            val logFunc = object : Function("log", 1) {
                override fun apply(vararg args: Double): Double {
                    return log10(args[0])
                }
            }

            val exp = ExpressionBuilder(finalExpression)
                .functions(sinFunc, cosFunc, tanFunc, asinFunc, acosFunc, atanFunc, lnFunc, logFunc, factFunc)
                .build()

            val result = exp.evaluate()

            if (result.isNaN() || result.isInfinite()) {
                Result.failure(ArithmeticException("Error"))
            } else {
                Result.success(result)
            }
        } catch (e: ArithmeticException) {
            // Divide by zero or math errors
            Result.failure(Exception("Error"))
        } catch (e: Exception) {
            Result.failure(Exception("Error"))
        }
    }
}
