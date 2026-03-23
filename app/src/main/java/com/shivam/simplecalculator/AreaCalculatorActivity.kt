package com.shivam.simplecalculator

import android.os.Bundle
import com.shivam.simplecalculator.databinding.ActivityAreaCalculatorBinding

class AreaCalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityAreaCalculatorBinding
    private var currentInput = "0"
    private var isInput1Active = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAreaCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupNumpad()
        setupInputSelection()
    }

    private fun setupInputSelection() {
        binding.cardInput1.setOnClickListener {
            isInput1Active = true
            currentInput = binding.tvValue1.text.toString()
            updateCursor()
        }
        binding.cardInput2.setOnClickListener {
            isInput1Active = false
            currentInput = binding.tvValue2.text.toString()
            updateCursor()
        }
    }

    private fun setupNumpad() {
        val buttons = listOf(
            binding.numpad.btn0, binding.numpad.btn1, binding.numpad.btn2,
            binding.numpad.btn3, binding.numpad.btn4, binding.numpad.btn5,
            binding.numpad.btn6, binding.numpad.btn7, binding.numpad.btn8,
            binding.numpad.btn9, binding.numpad.btnDot
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                com.shivam.simplecalculator.util.VibrationUtil.vibrate(this)
                onDigitPressed(getButtonText(button))
            }
        }

        binding.numpad.btnAC.setOnClickListener {
            com.shivam.simplecalculator.util.VibrationUtil.vibrate(this)
            currentInput = "0"
            updateUI()
        }

        binding.numpad.btnDel.setOnClickListener {
            com.shivam.simplecalculator.util.VibrationUtil.vibrate(this)
            if (currentInput.length > 1) {
                currentInput = currentInput.substring(0, currentInput.length - 1)
            } else {
                currentInput = "0"
            }
            updateUI()
        }
    }

    private fun onDigitPressed(digit: String) {
        if (currentInput == "0" && digit != ".") {
            currentInput = digit
        } else {
            if (digit == "." && currentInput.contains(".")) return
            currentInput += digit
        }
        updateUI()
    }

    private fun updateUI() {
        if (isInput1Active) {
            binding.tvValue1.text = currentInput
            calculateConversion()
        } else {
            binding.tvValue2.text = currentInput
            calculateConversionReverse()
        }
    }

    private fun updateCursor() {
        // Just visual feedback if needed, currently indicated by state
    }

    private fun calculateConversion() {
        val valueStr = currentInput.ifEmpty { "0" }
        if (valueStr == ".") return
        try {
            val value = java.math.BigDecimal(valueStr)
            val result = value.multiply(java.math.BigDecimal("10000"))
            binding.tvValue2.text = formatResult(result)
        } catch (e: Exception) {
            binding.tvValue2.text = "Error"
        }
    }

    private fun calculateConversionReverse() {
        val valueStr = currentInput.ifEmpty { "0" }
        if (valueStr == ".") return
        try {
            val value = java.math.BigDecimal(valueStr)
            val result = value.divide(java.math.BigDecimal("10000"), 8, java.math.RoundingMode.HALF_UP).stripTrailingZeros()
            binding.tvValue1.text = result.toPlainString()
        } catch (e: Exception) {
            binding.tvValue1.text = "Error"
        }
    }

    private fun formatResult(result: java.math.BigDecimal): String {
        return result.stripTrailingZeros().toPlainString()
    }
}
