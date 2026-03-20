package com.shivam.simplecalculator.ui.currency

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shivam.simplecalculator.BaseActivity
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityCurrencyConverterBinding
import com.shivam.simplecalculator.models.CurrencyModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrencyActivity : BaseActivity() {

    private lateinit var binding: ActivityCurrencyConverterBinding
    private val viewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // Currency Selection
        binding.cardBase.setOnClickListener { showBottomSheet(0) }
        binding.cardConverted1.setOnClickListener { showBottomSheet(1) }
        binding.cardConverted2.setOnClickListener { showBottomSheet(2) }

        // Keypad
        val numpad = binding.numpad
        val numberButtons = listOf(
            numpad.btn0, numpad.btn1, numpad.btn2, numpad.btn3,
            numpad.btn4, numpad.btn5, numpad.btn6, numpad.btn7,
            numpad.btn8, numpad.btn9, numpad.btnDot
        )

        numberButtons.forEach { button ->
            button.setOnClickListener {
                val char = getButtonText(it)
                handleInput(char)
            }
        }

        numpad.btnAC.setOnClickListener {
            viewModel.onInputAmountChanged("0")
        }

        numpad.btnDel.setOnClickListener {
            val current = viewModel.uiState.value.inputAmount
            if (current.isNotEmpty() && current != "0") {
                val newAmount = if (current.length == 1) "0" else current.dropLast(1)
                viewModel.onInputAmountChanged(newAmount)
            }
        }
    }

    private fun handleInput(char: String) {
        val current = viewModel.uiState.value.inputAmount
        if (char == "." && current.contains(".")) return

        // Limit to 18 digits (excluding decimal point)
        if (char != "." && current.filter { it.isDigit() }.length >= 18) return
        
        val newAmount = if (current == "0") {
            if (char == ".") "0." else char
        } else {
            current + char
        }
        viewModel.onInputAmountChanged(newAmount)
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: CurrencyState) {
        // Base
        binding.tvBaseCurrencyCode.text = state.baseCurrency?.currencyCode ?: "---"
        binding.tvBaseValue.text = state.inputAmount
        binding.tvBaseFullName.text = state.baseCurrency?.let { 
            "${it.currencyName} (${it.currencySign}) - ${it.countryName ?: ""}" 
        } ?: "Select Currency"

        // Converted 1
        binding.tvConverted1Code.text = state.convertedCurrency1?.currencyCode ?: "---"
        binding.tvConverted1Value.text = state.result1
        binding.tvConverted1FullName.text = state.convertedCurrency1?.let { 
            "${it.currencyName} (${it.currencySign}) - ${it.countryName ?: ""}" 
        } ?: "Select Currency"

        // Converted 2
        binding.tvConverted2Code.text = state.convertedCurrency2?.currencyCode ?: "---"
        binding.tvConverted2Value.text = state.result2
        binding.tvConverted2FullName.text = state.convertedCurrency2?.let { 
            "${it.currencyName} (${it.currencySign}) - ${it.countryName ?: ""}" 
        } ?: "Select Currency"
        
        // Highlight active input (always base for now as per requirements)
        binding.cardBase.setCardBackgroundColor(getColor(R.color.gst_selection_bg))
    }

    private fun showBottomSheet(position: Int) {
        val state = viewModel.uiState.value
        val selected = when (position) {
            0 -> state.baseCurrency
            1 -> state.convertedCurrency1
            2 -> state.convertedCurrency2
            else -> null
        }
        
        val bottomSheet = CurrencyBottomSheetFragment(
            state.currencies,
            selected
        ) { currency ->
            viewModel.onCurrencySelected(position, currency)
        }
        bottomSheet.show(supportFragmentManager, "CurrencySelection")
    }
}
