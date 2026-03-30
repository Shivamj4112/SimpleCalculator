package com.shivam.simplecalculator.domain.util

object InputValidator {
    private val operators = listOf("+", "−", "×", "÷", "^", "%")
    private val functions = listOf("sin", "cos", "tan", "asin", "acos", "atan", "log", "ln", "sqrt", "√", "sin-1", "cos-1", "tan-1")
    private val constants = listOf("π", "e")

    fun isOperator(char: String): Boolean = operators.contains(char)

    fun isNumber(char: String): Boolean = char.matches("^[0-9]+$".toRegex()) || constants.contains(char)
    
    fun getLastToken(expression: String): String {
        if (expression.isEmpty()) return ""
        val lastChar = expression.last().toString()
        if (isOperator(lastChar) || lastChar == "(" || lastChar == ")") return lastChar
        
        // Find the last continuous number or function
        var i = expression.length - 1
        while (i >= 0) {
            val c = expression[i].toString()
            if (isOperator(c) || c == "(" || c == ")") {
                break
            }
            i--
        }
        return expression.substring(i + 1)
    }

    fun canAppendDecimal(expression: String): Boolean {
        val lastToken = getLastToken(expression)
        return !lastToken.contains(".")
    }

    fun formatAppendDecimal(expression: String): String {
        if (expression.isEmpty()) return "0."
        val lastChar = expression.last().toString()
        if (isOperator(lastChar) || lastChar == "(") {
            return expression + "0."
        }
        if (lastChar == ")") {
            return expression + "×0."
        }
        if (canAppendDecimal(expression)) {
            return expression + "."
        }
        return expression
    }

    fun formatAppendNumber(expression: String, number: String): String {
        if (expression.isEmpty()) {
            return if (number == "00") "0" else number
        }

        val lastChar = expression.last().toString()
        val lastToken = getLastToken(expression)
        
        // Prevent leading multiple zeros like "0005"
        if (lastToken == "0") {
            if (number == "0" || number == "00") return expression // Do not append more zeros
            // Replace "0" with "5"
            return expression.dropLast(1) + number
        }

        // Auto add multiply if after a closing parenthesis
        if (lastChar == ")" || constants.contains(lastChar)) {
            return expression + "×" + number
        }

        return expression + number
    }

    fun formatAppendOperator(expression: String, operator: String): String {
        if (expression.isEmpty()) {
            // Allow unary minus at start
            if (operator == "−" || operator == "-") return "−"
            return "" // Prevent other operators at start
        }

        val lastChar = expression.last().toString()
        if (isOperator(lastChar)) {
            // Replace last operator if user types another
            if (expression.length == 1 && (lastChar == "−" || lastChar == "-")) {
                // If it's just a unary minus and we try to replace it, empty it
                if (operator == "−" || operator == "-") return expression
                return ""
            }
            return expression.dropLast(1) + operator
        }

        if (lastChar == "(") {
            // Only allow unary minus after '('
            if (operator == "−" || operator == "-") return expression + "−"
            return expression
        }

        // If ends with a decimal, append 0 first or drop the decimal
        var newExpr = expression
        if (lastChar == ".") {
            newExpr = expression.dropLast(1)
        }

        return newExpr + operator
    }

    fun formatAppendParenthesis(expression: String, openCount: Int): Pair<String, Int> {
        if (expression.isEmpty()) {
            return Pair("(", openCount + 1)
        }

        val lastChar = expression.last().toString()

        // Rules for '('
        // If last char is a number, constant, or ')', auto-add multiplication
        // "2(" -> "2×("
        // If last char is an operator or '(', just append '('
        
        // Let's decide if it should be an open or close parenthesis based on context.
        // Classic calculators have "( )" as one button or separate buttons.
        // Assuming user inputs "(" or ")":
        // This function handles a single "()" button toggle behavior or specific parenthesis.
        
        throw UnsupportedOperationException("Use specific appendOpen / appendClose logic in ExpressionManager")
    }
}
