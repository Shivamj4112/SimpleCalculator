package com.shivam.simplecalculator.util

object CalculatorEngine {
    
    fun evaluate(expression: String, isDeg: Boolean): Result<Double> {
        var sanitized = expression.trim()
        
        if (sanitized.isEmpty()) return Result.success(0.0)

        // 1. Remove trailing operators, decimal point, or opening parenthesis that are standalone
        while (sanitized.isNotEmpty() && (sanitized.last().toString().matches(Regex("[+\\-×÷%^.]")) || sanitized.last() == '(')) {
            // Wait, if it's "sin(", we don't want to just drop '(', we might want to drop "sin(" entirely if it's incomplete
            // For simplicity, dropping trailing operators gets us closer to a valid expression
            sanitized = sanitized.dropLast(1)
        }

        if (sanitized.isEmpty()) return Result.success(0.0)

        // 2. Auto-close parentheses
        val openCount = sanitized.count { it == '(' }
        val closeCount = sanitized.count { it == ')' }
        if (openCount > closeCount) {
            sanitized += ")".repeat(openCount - closeCount)
        }

        return try {
            val result = MathEvaluator.evaluate(sanitized, isDeg)
            if (result.isNaN() || result.isInfinite()) {
               Result.failure(ArithmeticException("Cannot divide by zero"))
            } else {
               Result.success(result)
            }
        } catch (e: ArithmeticException) {
            // Specific message for divide by zero
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Invalid Expression"))
        }
    }
}
