package com.shivam.simplecalculator.domain.util

class ExpressionManager {
    companion object {
        private const val OP = "[+\\-−×÷%^]"
        private const val OP_PARENS = "[+\\-−×÷%^()]"
        private const val OP_OPEN_PAREN = "[+\\-−×÷%^(]"
    }

    var expression: String = ""
        private set

    fun append(input: String, position: Int): Int {
        var currentPos = position
        if (input == "1/") {
            return appendInvX(currentPos)
        }

        if (input.matches(Regex("[0-9]")) || input == "00") {
            currentPos = appendNumber(input, currentPos)
        } else if (input == ".") {
            currentPos = appendDecimal(currentPos)
        } else if (input.matches(Regex(OP))) {
            currentPos = appendOperator(input, currentPos)
        } else if (input == "(" || input == ")") {
            currentPos = appendParenthesis(input, currentPos)
        } else {
            // Function or constant
            currentPos = appendFunctionOrConstant(input, currentPos)
        }
        return currentPos
    }

    private fun appendNumber(number: String, position: Int): Int {
        var pos = position
        if (expression.isEmpty()) {
            expression = if (number == "00") "0" else number
            return expression.length
        }

        val lastChar = if (pos > 0) expression[pos - 1] else null
        
        // Auto convert leading "0" to new number if not a decimal
        var tokenStart = pos - 1
        while (tokenStart >= 0 && !expression[tokenStart].toString().matches(Regex(OP_PARENS))) {
            tokenStart--
        }
        tokenStart++
        val currentToken = expression.substring(tokenStart, pos)
        
        if (currentToken == "0") {
            if (number == "0" || number == "00") return pos // Prevent "00" on "0"
            expression = expression.substring(0, pos - 1) + number + expression.substring(pos)
            return pos - 1 + number.length
        }

        if (lastChar == ')' || lastChar == 'π' || lastChar == 'e' || lastChar == '!') {
            expression = expression.substring(0, pos) + number + expression.substring(pos)
            return pos + number.length
        }

        expression = expression.substring(0, pos) + number + expression.substring(pos)
        return pos + number.length
    }

    private fun appendDecimal(position: Int): Int {
        var pos = position
        if (expression.isEmpty()) {
            expression = "0."
            return expression.length
        }
        
        val lastChar = if (pos > 0) expression[pos - 1] else null
        
        if (lastChar != null && lastChar.toString().matches(Regex(OP_OPEN_PAREN))) {
            val toInsert = "0."
            expression = expression.substring(0, pos) + toInsert + expression.substring(pos)
            return pos + toInsert.length
        }
        
        if (lastChar == ')' || lastChar == 'π' || lastChar == 'e' || lastChar == '!') {
            val toInsert = "0."
            expression = expression.substring(0, pos) + toInsert + expression.substring(pos)
            return pos + toInsert.length
        }

        var tokenStart = pos - 1
        while (tokenStart >= 0 && !expression[tokenStart].toString().matches(Regex(OP_PARENS))) {
            tokenStart--
        }
        tokenStart++
        val currentToken = expression.substring(tokenStart, pos)

        if (!currentToken.contains(".")) {
            if(currentToken.count { it == '.' } == 0) {
               expression = expression.substring(0, pos) + "." + expression.substring(pos)
               return pos + 1
            }
        }
        return pos
    }

    private fun appendOperator(operator: String, position: Int): Int {
        var pos = position
        if (expression.isEmpty()) {
            if (operator == "−" || operator == "-") {
                expression = "−"
                return 1
            }
            return 0
        }
        
        val lastChar = if (pos > 0) expression[pos - 1] else null
        val nextChar = if (pos < expression.length) expression[pos] else null

        // If trying to insert before another operator, replace the next operator instead
        if (nextChar != null && nextChar.toString().matches(Regex(OP))) {
            if (operator == "−" || operator == "-") {
                // Allow e.g. typing minus before an operator if it makes sense, but normally we just replace
            }
            expression = expression.substring(0, pos) + operator + expression.substring(pos + 1)
            return pos + 1
        }

        if (lastChar != null && lastChar.toString().matches(Regex(OP))) {
            if ((lastChar == '×' || lastChar == '÷' || lastChar == '^') && (operator == "−" || operator == "-")) {
                 expression = expression.substring(0, pos) + "−" + expression.substring(pos)
                 return pos + 1
            }
            if (pos == 1 && (lastChar == '−' || lastChar == '-')) {
                if (operator == "−" || operator == "-") return pos
                return pos 
            }
            val secondLastChar = if (pos > 1) expression[pos - 2] else null
            if (secondLastChar != null && secondLastChar.toString().matches(Regex(OP))) {
                if (operator != "−" && operator != "-") {
                    expression = expression.substring(0, pos - 2) + operator + expression.substring(pos)
                    return pos - 1
                } else {
                     return pos
                }
            }
            expression = expression.substring(0, pos - 1) + operator + expression.substring(pos)
            return pos
        }
        
        if (lastChar == '(') {
            if (operator == "−" || operator == "-") {
                expression = expression.substring(0, pos) + "−" + expression.substring(pos)
                return pos + 1
            }
            // MODIFIED: Don't agressively block operators after '(' anymore. Enable free typing.
        }
        
        var newPos = pos
        if (lastChar == '.') {
            expression = expression.substring(0, pos - 1) + expression.substring(pos)
            newPos--
        }
        
        expression = expression.substring(0, newPos) + operator + expression.substring(newPos)
        return newPos + 1
    }

    private fun appendParenthesis(paren: String, position: Int): Int {
        var pos = position
        if (paren == "(") {
            if (pos > 0) {
                val lastChar = expression[pos - 1]
                if (lastChar.isDigit() || lastChar == ')' || lastChar == '.' || lastChar == 'π' || lastChar == 'e' || lastChar == '!') {
            // Remove aggressive '×' insertion so it looks like 2( instead of 2×(
                    var finalPos = pos
                    if (lastChar == '.') {
                        expression = expression.substring(0, pos - 1) + expression.substring(pos)
                        finalPos--
                    }
                    val toInsert = "("
                    expression = expression.substring(0, finalPos) + toInsert + expression.substring(finalPos)
                    return finalPos + toInsert.length
                }
                // MODIFIED: We no longer aggressively block multiple consecutive `(`. Wait, actually (( is valid.
            }
            expression = expression.substring(0, pos) + "(" + expression.substring(pos)
            return pos + 1
        } else if (paren == ")") {
            val openCount = expression.substring(0, pos).count { it == '(' }
            val closeCount = expression.substring(0, pos).count { it == ')' }
            // MODIFIED: Allow natural typing of ) even if openCount <= closeCount
            if (pos > 0) {
                val lastChar = expression[pos - 1]
                var finalPos = pos
                if (lastChar == '.') {
                    expression = expression.substring(0, pos - 1) + expression.substring(pos)
                    finalPos--
                }
                expression = expression.substring(0, finalPos) + ")" + expression.substring(finalPos)
                return finalPos + 1
            }
        }
        return pos
    }

    private fun appendFunctionOrConstant(input: String, position: Int): Int {
        var pos = position
        if (pos > 0) {
            val lastChar = expression[pos - 1]
            
            if (input == "!") {
                if (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e') {
                    expression = expression.substring(0, pos) + "!" + expression.substring(pos)
                    return pos + 1
                }
                return pos
            }
            
            if (lastChar.isDigit() || lastChar == ')' || lastChar == '.' || lastChar == 'π' || lastChar == 'e' || lastChar == '!') {
                var finalPos = pos
                if (lastChar == '.') {
                    expression = expression.substring(0, pos - 1) + expression.substring(pos)
                    finalPos--
                }
                val toInsert = input
                expression = expression.substring(0, finalPos) + toInsert + expression.substring(finalPos)
                return finalPos + toInsert.length
            }
        }
        expression = expression.substring(0, pos) + input + expression.substring(pos)
        return pos + input.length
    }
    
    fun backspace(position: Int): Int {
        var pos = position
        if (pos > 0 && expression.isNotEmpty()) {
            val func3 = listOf("sin(", "cos(", "tan(", "log(")
            val func4 = listOf("asin(", "acos(", "atan(")
            val func5 = listOf("sin-1(", "cos-1(", "tan-1(")
            
            val substringBefore = expression.substring(0, pos)
            
            for (f in func5) {
               if (substringBefore.endsWith(f)) {
                   expression = expression.substring(0, pos - f.length) + expression.substring(pos)
                   return pos - f.length
               }
            }
            for (f in func4) {
               if (substringBefore.endsWith(f)) {
                   expression = expression.substring(0, pos - f.length) + expression.substring(pos)
                   return pos - f.length
               }
            }            
            for (f in func3) {
               if (substringBefore.endsWith(f)) {
                   expression = expression.substring(0, pos - f.length) + expression.substring(pos)
                   return pos - f.length
               }
            }
            if (substringBefore.endsWith("ln(")) {
                expression = expression.substring(0, pos - 3) + expression.substring(pos)
                return pos - 3
            }
            if (substringBefore.endsWith("^(−1)")) {
                expression = expression.substring(0, pos - 5) + expression.substring(pos)
                return pos - 5
            }
            if (substringBefore.endsWith("1/(")) {
                expression = expression.substring(0, pos - 3) + expression.substring(pos)
                return pos - 3
            }
            if (substringBefore.endsWith("1/")) {
                expression = expression.substring(0, pos - 2) + expression.substring(pos)
                return pos - 2
            }
            
            expression = expression.substring(0, pos - 1) + expression.substring(pos)
            return pos - 1
        }
        return pos
    }

    fun clear() {
        expression = ""
    }

    fun setExpression(expr: String) {
        expression = expr
    }

    private fun appendInvX(position: Int): Int {
        val replacement = "^(−1)"
        expression = expression.substring(0, position) + replacement + expression.substring(position)
        return position + replacement.length
    }
}
