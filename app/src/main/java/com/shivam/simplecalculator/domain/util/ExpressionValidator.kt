package com.shivam.simplecalculator.domain.util

object ExpressionValidator {

    private val validFunctions = listOf("sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "sqrt", "√")
    private val operators = listOf('+', '-', '−', '×', '÷', '^')

    fun isValid(expression: String): Boolean {
        if (expression.isBlank()) return true

        // 1. Unbalanced parentheses
        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        if (openCount != closeCount) return false

        // 2. Empty parentheses ()
        if (expression.contains("()")) return false

        // 3. Functions without arguments like sin()
        for (func in validFunctions) {
            if (expression.contains("$func()")) return false
        }

        // 4. Starts with invalid operator (allowing +, -, − at start)
        val firstChar = expression.first()
        if (firstChar in listOf('×', '÷', '%', '^')) return false

        // 5. Ends with an operator (excluding % as it's often a suffix)
        val lastChar = expression.last()
        if (lastChar in operators) return false

        // 6. Invalid consecutive operators (e.g., ++, ** (××), +×)
        for (i in 0 until expression.length - 1) {
            val current = expression[i]
            val next = expression[i + 1]
            if (current in operators && next in operators) {
                return false
            }
            // Cannot have operator followed by % or ! (like +%)
            if (current in operators && (next == '%' || next == '!')) {
                return false
            }
        }

        return true
    }
}
