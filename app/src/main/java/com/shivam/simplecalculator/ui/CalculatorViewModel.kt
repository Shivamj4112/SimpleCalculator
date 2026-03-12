package com.shivam.simplecalculator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.simplecalculator.data.CalculationHistory
import com.shivam.simplecalculator.data.HistoryRepository
import com.shivam.simplecalculator.util.MathEvaluator
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

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    // Configuration modes
    var isDegMode = true
    var isInverseMode = false // 2nd button

    val history = repository.getAllHistory()

    private val formatter = DecimalFormat("#.##########")

    fun append(char: String) {
        if (_result.value == "Input Error" || _result.value == "Divide by zero") {
            _result.value = ""
        }
        
        // Auto-clear if result was present and user types a number (starts a new calculation)
        if (_result.value.isNotEmpty() && !char.matches(Regex("[+\\-×÷%^]"))) {
            _expression.value = ""
            _result.value = ""
        }

        if (_result.value.isNotEmpty() && char.matches(Regex("[+\\-×÷%^]"))) {
            // Append operator to old result
            _expression.value = _result.value + char
            _result.value = ""
            return
        }

        _expression.value += char
    }

    fun backspace() {
        val current = _expression.value
        if (current.isNotEmpty()) {
            _expression.value = current.dropLast(1)
        }
    }

    fun clear() {
        _expression.value = ""
        _result.value = ""
    }

    fun calculate() {
        if (_expression.value.isEmpty()) return

        try {
            val res = MathEvaluator.evaluate(_expression.value, isDegMode)
            val parsedResult = formatter.format(res)
            _result.value = parsedResult
            
            // Save to DB
            viewModelScope.launch {
                repository.insert(CalculationHistory(
                    expression = _expression.value,
                    result = parsedResult
                ))
            }
        } catch (e: ArithmeticException) {
            _result.value = "Input Error"
        }
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
