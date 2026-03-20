package com.shivam.simplecalculator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.simplecalculator.data.CalculationHistory
import com.shivam.simplecalculator.data.HistoryRepository
import com.shivam.simplecalculator.util.CalculatorEngine
import com.shivam.simplecalculator.util.ExpressionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {

    private val expressionManager = ExpressionManager()

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    // Configuration modes
    var isDegMode = true
    var isInverseMode = false // 2nd button

    val history = repository.getAllHistory()

    private val formatter = DecimalFormat("#.##########")
    
    private var isCalculated = false

    fun append(char: String) {
        if (_result.value == "Invalid Expression" || _result.value == "Cannot divide by zero") {
            _result.value = ""
        }
        
        // Auto-clear if result was present and user types a number or function
        if (isCalculated) {
            if (!char.matches(Regex("[+\\-×÷%^]"))) {
                expressionManager.clear()
            } else {
                // Keep the result as the new expression base
                expressionManager.setExpression(_result.value.replace(",", ""))
            }
            isCalculated = false
        }

        expressionManager.append(char)
        _expression.value = expressionManager.expression
        
        updateRealTimeResult()
    }

    fun backspace() {
        if (isCalculated) {
            isCalculated = false
        }
        expressionManager.backspace()
        _expression.value = expressionManager.expression
        updateRealTimeResult()
    }

    fun clear() {
        expressionManager.clear()
        _expression.value = ""
        _result.value = ""
        isCalculated = false
    }

    fun setExpression(expr: String) {
        expressionManager.setExpression(expr)
        _expression.value = expressionManager.expression
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
            _result.value = formatter.format(res)
        } else {
            // Keep previous valid Real Time result if invalid currently, or show empty
            _result.value = ""
        }
    }

    fun calculate() {
        if (expressionManager.expression.isEmpty()) return

        val resultAttempt = CalculatorEngine.evaluate(expressionManager.expression, isDegMode)
        if (resultAttempt.isSuccess) {
            val res = resultAttempt.getOrNull() ?: 0.0
            val parsedResult = formatter.format(res)
            _result.value = parsedResult
            
            isCalculated = true
            
            // Auto close brackets visually in expression
            expressionManager.setExpression(autoCloseBrackets(expressionManager.expression))
            _expression.value = expressionManager.expression
            
            viewModelScope.launch {
                repository.insert(CalculationHistory(
                    expression = expressionManager.expression,
                    result = parsedResult
                ))
            }
        } else {
            val error = resultAttempt.exceptionOrNull()
            _result.value = error?.message ?: "Invalid Expression"
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
