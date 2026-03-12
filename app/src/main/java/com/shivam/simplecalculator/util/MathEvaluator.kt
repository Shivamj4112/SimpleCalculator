package com.shivam.simplecalculator.util

import kotlin.math.*

object MathEvaluator {

    fun evaluate(expression: String, isDeg: Boolean = true): Double {
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())

        return evaluateExpression(sanitized, isDeg)
    }

    private fun evaluateExpression(expr: String, isDeg: Boolean): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expr.length) expr[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < expr.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Divide by zero")
                        x /= divisor
                    } else if (eat('%'.code)) {
                        x %= parseFactor()
                    } else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus

                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = expr.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code || ch >= 'A'.code && ch <= 'Z'.code) { // functions
                    while (ch >= 'a'.code && ch <= 'z'.code || ch >= 'A'.code && ch <= 'Z'.code || ch == '-'.code || ch == '1'.code) nextChar()
                    val func = expr.substring(startPos, pos)
                    x = parseFactor()
                    var xRad = x
                    if (isDeg) {
                        xRad = Math.toRadians(x)
                    }

                    x = when (func) {
                        "sqrt", "√" -> sqrt(x)
                        "sin" -> sin(xRad)
                        "cos" -> cos(xRad)
                        "tan" -> tan(xRad)
                        "asin", "sin-1" -> if (isDeg) Math.toDegrees(asin(x)) else asin(x)
                        "acos", "cos-1" -> if (isDeg) Math.toDegrees(acos(x)) else acos(x)
                        "atan", "tan-1" -> if (isDeg) Math.toDegrees(atan(x)) else atan(x)
                        "log" -> log10(x)
                        "ln" -> ln(x)
                        else -> throw RuntimeException("Unknown function: $func")
                    }
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                if (eat('^'.code)) x = x.pow(parseFactor()) // exponentiation
                if (eat('!'.code)) x = factorial(x.toInt()).toDouble() // factorial

                return x
            }
        }.parse()
    }
    
    fun factorial(n: Int): Long {
        if (n < 0) throw RuntimeException("Negative factorial")
        var res = 1L
        for (i in 2..n) res *= i
        return res
    }
}
