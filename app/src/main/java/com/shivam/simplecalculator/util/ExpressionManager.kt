package com.shivam.simplecalculator.util

class ExpressionManager {
    var expression: String = ""
        private set

    fun append(input: String) {
        if (input.matches(Regex("[0-9]")) || input == "00") {
            appendNumber(input)
        } else if (input == ".") {
            appendDecimal()
        } else if (input.matches(Regex("[+\\-×÷%^]"))) {
            appendOperator(input)
        } else if (input == "(" || input == ")") {
            appendParenthesis(input)
        } else {
            // Function or constant
            appendFunctionOrConstant(input)
        }
    }

    private fun appendNumber(number: String) {
        if (expression.isEmpty()) {
            expression = if (number == "00") "0" else number
            return
        }

        val lastChar = expression.last()
        
        // Auto convert leading "0" to new number if not a decimal
        val tokens = expression.split(Regex("[+\\-×÷%^()]"))
        val lastToken = tokens.lastOrNull() ?: ""
        
        if (lastToken == "0") {
            if (number == "0" || number == "00") return // Prevent "00" on "0"
            expression = expression.dropLast(1) + number // "0" -> "5"
            return
        }

        if (lastChar == ')' || lastChar == 'π' || lastChar == 'e' || lastChar == '!') {
            expression += "×$number"
            return
        }

        expression += number
    }

    private fun appendDecimal() {
        if (expression.isEmpty()) {
            expression = "0."
            return
        }
        
        val lastChar = expression.last()
        if (lastChar.toString().matches(Regex("[+\\-×÷%^(]"))) {
            expression += "0."
            return
        }
        
        if (lastChar == ')' || lastChar == 'π' || lastChar == 'e' || lastChar == '!') {
            expression += "×0."
            return
        }

        val tokens = expression.split(Regex("[+\\-×÷%^()]"))
        val lastToken = tokens.lastOrNull() ?: ""

        if (!lastToken.contains(".")) {
            expression += "."
        }
    }

    private fun appendOperator(operator: String) {
        if (expression.isEmpty()) {
            if (operator == "−" || operator == "-") expression = "−"
            return
        }
        
        val lastChar = expression.last()
        
        if (lastChar.toString().matches(Regex("[+\\-×÷%^]"))) {
            if (expression.length == 1 && (lastChar == '−' || lastChar == '-')) {
                if (operator == "−" || operator == "-") return
            } else {
                expression = expression.dropLast(1) + operator
            }
            return
        }
        
        if (lastChar == '(') {
            if (operator == "−" || operator == "-") expression += "−"
            return
        }
        
        if (lastChar == '.') {
            expression = expression.dropLast(1)
        }
        
        expression += operator
    }

    private fun appendParenthesis(paren: String) {
        if (paren == "(") {
            if (expression.isNotEmpty()) {
                val lastChar = expression.last()
                if (lastChar.isDigit() || lastChar == ')' || lastChar == '.' || lastChar == 'π' || lastChar == 'e' || lastChar == '!') {
                    if (lastChar == '.') expression = expression.dropLast(1)
                    expression += "×("
                    return
                }
            }
            expression += "("
        } else if (paren == ")") {
            val openCount = expression.count { it == '(' }
            val closeCount = expression.count { it == ')' }
            if (openCount > closeCount) {
                val lastChar = expression.last()
                if (lastChar.toString().matches(Regex("[+\\-×÷%^(]"))) return // Prevent `()`, `+)`, etc.
                if (lastChar == '.') expression = expression.dropLast(1)
                expression += ")"
            }
        }
    }

    private fun appendFunctionOrConstant(input: String) {
        if (expression.isNotEmpty()) {
            val lastChar = expression.last()
            
            if (input == "!") {
                if (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e') {
                    expression += "!"
                }
                return
            }
            
            if (lastChar.isDigit() || lastChar == ')' || lastChar == '.' || lastChar == 'π' || lastChar == 'e' || lastChar == '!') {
                if (lastChar == '.') expression = expression.dropLast(1)
                expression += "×$input"
                return
            }
        }
        expression += input
    }
    
    fun backspace() {
        if (expression.isNotEmpty()) {
            val func3 = listOf("sin", "cos", "tan", "log")
            val func4 = listOf("asin", "acos", "atan")
            val func5 = listOf("sin-1", "cos-1", "tan-1")
            
            for (f in func5) {
               if (expression.endsWith("$f(")) {
                   expression = expression.dropLast(6)
                   return
               }
            }
            for (f in func4) {
               if (expression.endsWith("$f(")) {
                   expression = expression.dropLast(5)
                   return
               }
            }            
            for (f in func3) {
               if (expression.endsWith("$f(")) {
                   expression = expression.dropLast(4)
                   return
               }
            }
            if (expression.endsWith("ln(")) {
                expression = expression.dropLast(3)
                return
            }
            if (expression.endsWith("1/")) {
                expression = expression.dropLast(2)
                return
            }
            if (expression.endsWith("√")) {
                expression = expression.dropLast(1)
                return
            }
            
            expression = expression.dropLast(1)
        }
    }

    fun clear() {
        expression = ""
    }

    fun setExpression(expr: String) {
        expression = expr
    }
}
