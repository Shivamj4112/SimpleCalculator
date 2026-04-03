package com.shivam.simplecalculator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shivam.simplecalculator.data.model.CurrencyState
import com.shivam.simplecalculator.data.repository.CurrencyRepository
import com.shivam.simplecalculator.domain.models.CurrencyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.shivam.simplecalculator.domain.util.NetworkUtils
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale
import javax.inject.Inject



@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyState())
    val uiState: StateFlow<CurrencyState> = _uiState.asStateFlow()

    init {
        fetchCurrencies()
    }

    fun fetchCurrencies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            var hasEmittedData = false
            
            repository.getCurrencies()
                .catch { e -> 
                    if (!hasEmittedData) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false, 
                            error = e.message ?: "An error occurred"
                        ) 
                    }
                }
                .onCompletion { 
                    _uiState.value = _uiState.value.copy(isLoading = false) 
                    if (!hasEmittedData) {
                        _uiState.value = _uiState.value.copy(error = "Connect to internet to fetch initial rates")
                    }
                }
                .collectLatest { currencies ->
                    if (currencies.isNotEmpty()) {
                        hasEmittedData = true
                        val base = _uiState.value.baseCurrency ?: currencies.find { it.currencyCode == "INR" } ?: currencies[0]
                        val c1 = _uiState.value.convertedCurrency1 ?: currencies.find { it.currencyCode == "USD" } ?: if (currencies.size > 1) currencies[1] else currencies[0]
                        val c2 = _uiState.value.convertedCurrency2 ?: currencies.find { it.currencyCode == "EUR" } ?: if (currencies.size > 2) currencies[2] else currencies[0]
                        
                        _uiState.value = _uiState.value.copy(
                            currencies = currencies,
                            baseCurrency = base,
                            convertedCurrency1 = c1,
                            convertedCurrency2 = c2,
                            isLoading = false,
                            error = null
                        )
                        calculateRates()
                    }
                }
        }
    }

    fun onInputAmountChanged(amount: String) {
        _uiState.value = _uiState.value.copy(inputAmount = amount)
        calculateRates()
    }

    fun onCurrencySelected(position: Int, currency: CurrencyModel) {
        when (position) {
            0 -> _uiState.value = _uiState.value.copy(baseCurrency = currency)
            1 -> _uiState.value = _uiState.value.copy(convertedCurrency1 = currency)
            2 -> _uiState.value = _uiState.value.copy(convertedCurrency2 = currency)
        }
        calculateRates()
    }

    private fun calculateRates() {
        val state = _uiState.value
        val input = state.inputAmount.toDoubleOrNull() ?: 0.0
        val baseRate = state.baseCurrency?.rate ?: 1.0
        
        val r1 = state.convertedCurrency1?.let { target ->
            (input / baseRate) * target.rate
        } ?: 0.0

        val r2 = state.convertedCurrency2?.let { target ->
            (input / baseRate) * target.rate
        } ?: 0.0

        _uiState.value = _uiState.value.copy(
            result1 = formatNumber(r1),
            result2 = formatNumber(r2)
        )
    }

    private fun formatNumber(number: Double): String {
        return if (number == 0.0) "0"
        else if (number % 1.0 == 0.0) String.format(Locale.US, "%.0f", number)
        else String.format(Locale.US, "%,.6f", number).trimEnd('0').trimEnd('.')
    }
}
