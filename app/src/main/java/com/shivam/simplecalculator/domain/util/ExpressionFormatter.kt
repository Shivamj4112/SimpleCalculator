package com.shivam.simplecalculator.domain.util

object ExpressionFormatter {

    fun format(rawExpression: String): String {
        val result = java.lang.StringBuilder()
        var currentNumber = java.lang.StringBuilder()

        for (i in rawExpression.indices) {
            val char = rawExpression[i]
            if (char.isDigit() || char == '.') {
                currentNumber.append(char)
            } else {
                if (currentNumber.isNotEmpty()) {
                    result.append(formatNumberToken(currentNumber.toString()))
                    currentNumber.clear()
                }
                result.append(char)
            }
        }
        if (currentNumber.isNotEmpty()) {
            result.append(formatNumberToken(currentNumber.toString()))
        }
        return result.toString()
    }

    fun getRawPosition(rawExpression: String, formattedPosition: Int): Int {
        val formattedExp = format(rawExpression)
        val validFormattedPos = formattedPosition.coerceIn(0, formattedExp.length)

        val substring = formattedExp.substring(0, validFormattedPos)
        val commaCount = substring.count { it == ',' }

        return validFormattedPos - commaCount
    }

    fun getFormattedPosition(rawExpression: String, rawPosition: Int): Int {
        val validRawPos = rawPosition.coerceIn(0, rawExpression.length)
        val result = java.lang.StringBuilder()
        var currentNumber = java.lang.StringBuilder()
        var tokenStartRawIndex = -1
        var formattedCursorPos = -1

        for (i in 0..rawExpression.length) {
            if (i == validRawPos) {
                if (currentNumber.isEmpty()) {
                    formattedCursorPos = result.length
                }
            }

            if (i < rawExpression.length) {
                val char = rawExpression[i]
                if (char.isDigit() || char == '.') {
                    if (currentNumber.isEmpty()) tokenStartRawIndex = i
                    currentNumber.append(char)
                } else {
                    if (currentNumber.isNotEmpty()) {
                        val formattedNum = formatNumberToken(currentNumber.toString())
                        if (validRawPos > tokenStartRawIndex && validRawPos <= i) {
                            val offsetInRawNumber = validRawPos - tokenStartRawIndex
                            val commasAdded = getCommasBeforeOffset(currentNumber.toString(), offsetInRawNumber)
                            formattedCursorPos = result.length + offsetInRawNumber + commasAdded
                        }
                        result.append(formattedNum)
                        currentNumber.clear()
                    }
                    if (i == validRawPos) {
                        formattedCursorPos = result.length
                    }
                    result.append(char)
                }
            } else {
                if (currentNumber.isNotEmpty()) {
                    val formattedNum = formatNumberToken(currentNumber.toString())
                    if (validRawPos > tokenStartRawIndex && validRawPos <= rawExpression.length) {
                        val offsetInRawNumber = validRawPos - tokenStartRawIndex
                        val commasAdded = getCommasBeforeOffset(currentNumber.toString(), offsetInRawNumber)
                        formattedCursorPos = result.length + offsetInRawNumber + commasAdded
                    }
                    result.append(formattedNum)
                }
            }
        }

        return formattedCursorPos
    }


    fun formatNumberToken(numberRaw: String): String {
        val dotIndex = numberRaw.indexOf('.')
        val integerPart = if (dotIndex == -1) numberRaw else numberRaw.substring(0, dotIndex)
        val decimalPart = if (dotIndex == -1) "" else numberRaw.substring(dotIndex)

        val formattedInteger = java.lang.StringBuilder()
        var count = 0
        for (i in integerPart.length - 1 downTo 0) {
            formattedInteger.append(integerPart[i])
            count++
            if (count == 3 && i > 0 && integerPart[i - 1].isDigit()) {
                formattedInteger.append(",")
                count = 0
            }
        }
        formattedInteger.reverse()
        return formattedInteger.toString() + decimalPart
    }

    private fun getCommasBeforeOffset(rawNumber: String, rawOffset: Int): Int {
        val dotIndex = rawNumber.indexOf('.')
        val integerPartLength = if (dotIndex == -1) rawNumber.length else dotIndex
        
        val totalCommas = maxOf(0, integerPartLength - 1) / 3

        return if (rawOffset <= integerPartLength) {
            val digitsToRight = integerPartLength - rawOffset
            val commasToRight = digitsToRight / 3
            totalCommas - commasToRight
        } else {
            totalCommas
        }
    }

    fun preprocessForEvaluation(expression: String): String {
        var processed = expression.trim()

        if (processed.isEmpty()) return processed

        // 1. Remove trailing operators or decimal
        while (processed.isNotEmpty() && (processed.last() in listOf('+', '-', '−', '×', '÷', '^', '.'))) {
            processed = processed.dropLast(1)
        }

        // 2. Fuse digits typed right after a ^(−X) bracket into the exponent itself
        // Example: 5^(−1)5 -> 5^(−15). This prevents implicit multiplication from turning it into (1/5)*5=1.
        processed = processed.replace(Regex("\\^\\((−?-?\\d+(?:\\.\\d+)?)\\)(\\d+(?:\\.\\d+)?)"), "^($1$2)")

        // 3. Auto-close parentheses
        val openCount = processed.count { it == '(' }
        val closeCount = processed.count { it == ')' }
        if (openCount > closeCount) {
            processed += ")".repeat(openCount - closeCount)
        }

        // 3. Implicit multiplication e.g., 2sin(30) -> 2*sin(30), 2(3) -> 2*(3), e(5) -> e*(5)
        processed = applyImplicitMultiplication(processed)

        // 4. Translate mathematical functions to exp4j standard names
        processed = processed
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("sin⁻¹", "asin")
            .replace("cos⁻¹", "acos")
            .replace("tan⁻¹", "atan")
            .replace("sin-1", "asin")
            .replace("cos-1", "acos")
            .replace("tan-1", "atan")
            .replace("π", "pi")
            .replace("√", "sqrt")
        
        // Convert postfix factorials x! to prefix fact(x).
        // Works for single numbers or closed parentheses groups.
        while (processed.contains("!")) {
            processed = processed.replace(Regex("([\\w.]+)!"), "fact($1)")
            processed = processed.replace(Regex("\\(([^()]+)\\)!"), "fact($1)")
        }

        return processed
    }

    private fun applyImplicitMultiplication(expr: String): String {
        var res = expr
        // Number followed by function: 2sin -> 2*sin
        res = res.replace(Regex("(\\d|π|e|%)(sin|cos|tan|sin⁻¹|cos⁻¹|tan⁻¹|asin|acos|atan|log|ln|sqrt|√)"), "$1*$2")
        // Postfix % followed by prefix √
        res = res.replace(Regex("%(√)"), "%*$1")
        // Number or constant or % followed by parenthesis: 2( -> 2*(, %( -> %*(
        res = res.replace(Regex("(\\d|π|e|\\)|%)\\("), "$1*(")
        // Parenthesis or % followed by number or constant: )2 -> )*2, %2 -> %*2
        res = res.replace(Regex("([\\)%])(\\d|π|e)"), "$1*$2")
        // Number followed by constant: 2π -> 2*π
        res = res.replace(Regex("(\\d)(π|e)"), "$1*$2")
        // Constant followed by Number: π2 -> π*2
        res = res.replace(Regex("(π|e)(\\d)"), "$1*$2")
        // Constant followed by Constant (overlapping supported via lookaround): ee -> e*e
        res = res.replace(Regex("(?<=[πe])(?=[πe])"), "*")
        return res
    }
}
