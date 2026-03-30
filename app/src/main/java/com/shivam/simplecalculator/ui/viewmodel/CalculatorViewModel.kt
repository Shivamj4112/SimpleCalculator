package com.shivam.simplecalculator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.simplecalculator.data.repository.CalculationHistory
import com.shivam.simplecalculator.data.repository.HistoryRepository
import com.shivam.simplecalculator.domain.util.CalculatorEngine
import com.shivam.simplecalculator.domain.util.ExpressionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {

    private val expressionManager = ExpressionManager()

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _selection = MutableStateFlow(0)
    val selection: StateFlow<Int> = _selection.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    // Configuration modes
    var isDegMode = true
    var isInverseMode = false // 2nd button

    val history = repository.getAllHistory()

    private val symbols = DecimalFormatSymbols(Locale.US)
    private val formatter = DecimalFormat("#,###.###########", symbols)
    private val scientificFormatter = DecimalFormat("0.##########E0", symbols)

    private var isCalculated = false

    fun append(char: String, position: Int) {
        if (_result.value == "Error") {
            _result.value = ""
        }

        // Auto-clear if result was present and user types a number or function
        if (isCalculated) {
            isCalculated = false
        }

        val newPos = expressionManager.append(char, position)
        _expression.value = expressionManager.expression
        _selection.value = newPos

        updateRealTimeResult()
    }

    fun backspace(position: Int) {
        if (isCalculated) {
            isCalculated = false
        }
        val newPos = expressionManager.backspace(position)
        _expression.value = expressionManager.expression
        _selection.value = newPos
        updateRealTimeResult()
    }

    fun clear() {
        expressionManager.clear()
        _expression.value = ""
        _selection.value = 0
        _result.value = ""
        isCalculated = false
    }

    fun setExpression(expr: String) {
        expressionManager.setExpression(expr)
        _expression.value = expressionManager.expression
        _selection.value = expressionManager.expression.length
        isCalculated = false
        updateRealTimeResult()
    }

    private fun updateRealTimeResult() {
        if (expressionManager.expression.isEmpty()) {
            _result.value = ""
            return
        }

        val resultAttempt = CalculatorEngine.evaluate(expressionManager.expression, isDegMode)
        if (resultAttempt.isSuccess) {
            val res = resultAttempt.getOrNull() ?: 0.0
            _result.value = formatResult(res)
        } else {
            _result.value = "" // Don't show error in real-time, just keep it clean or show nothing
        }
    }

    private fun formatResult(value: Double): String {
        var formatted = formatter.format(value)
        if (formatted.length > 19) {
            formatted = scientificFormatter.format(value)
        }
        return formatted
    }

    fun calculate() {
        if (expressionManager.expression.isEmpty()) return

        val resultAttempt = CalculatorEngine.evaluate(expressionManager.expression, isDegMode)
        if (resultAttempt.isSuccess) {
            val res = resultAttempt.getOrNull() ?: 0.0
            val parsedResult = formatResult(res)
            
            // Log original expression to history
            val originalExpression = expressionManager.expression
            viewModelScope.launch {
                repository.insert(
                    CalculationHistory(
                        expression = originalExpression,
                        result = parsedResult
                    )
                )
            }

            // Auto close brackets visually in expression (not strictly needed now as we move result to EXPR)
            autoCloseBrackets(originalExpression)
            
            // Move result to expression field
            expressionManager.setExpression(parsedResult.replace(",", ""))
            _expression.value = expressionManager.expression
            _selection.value = expressionManager.expression.length
            _result.value = "" // Clear result field as it's now in expression
            
            isCalculated = true
        } else {
            val error = resultAttempt.exceptionOrNull()
            _result.value = "Error"
            isCalculated = true
        }
    }

    private fun autoCloseBrackets(expr: String): String {
        var newExpr = expr
        val openCount = newExpr.count { it == '(' }
        val closeCount = newExpr.count { it == ')' }
        if (openCount > closeCount) {
            newExpr += ")".repeat(openCount - closeCount)
        }
        return newExpr
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun deleteHistoryItems(ids: List<Long>) {
        viewModelScope.launch {
            repository.deleteHistoryItems(ids)
        }
    }
}