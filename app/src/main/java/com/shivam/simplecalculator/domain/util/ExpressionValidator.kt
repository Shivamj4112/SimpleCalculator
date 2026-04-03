package com.shivam.simplecalculator.domain.util

object ExpressionValidator {

    private val validFunctions = listOf("sin", "cos", "tan", "sin⁻¹", "cos⁻¹", "tan⁻¹", "asin", "acos", "atan", "sin-1", "cos-1", "tan-1", "log", "ln", "sqrt", "√")
    private val operators = listOf('+', '-', '−', '×', '÷', '^')

    fun isValid(expression: String): Boolean {
        if (expression.isBlank()) return true

        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        if (openCount != closeCount) return false

        if (expression.contains("()")) return false

        for (func in validFunctions) {
            if (expression.contains("$func()")) return false
        }

        val firstChar = expression.first()
        if (firstChar in listOf('×', '÷', '%', '^')) return false

        val lastChar = expression.last()
        if (lastChar in operators) return false

        for (i in 0 until expression.length - 1) {
            val current = expression[i]
            val next = expression[i + 1]
            if (current in operators && next in operators) {
                return false
            }
            if (current in operators && (next == '%' || next == '!')) {
                return false
            }
        }

        return true
    }
}
