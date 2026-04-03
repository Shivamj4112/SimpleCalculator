package com.shivam.simplecalculator.ui.activites

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.shivam.simplecalculator.databinding.ActivityDiscountCalculatorBinding
import com.shivam.simplecalculator.domain.util.VibrationUtil
import com.shivam.simplecalculator.domain.util.ExpressionFormatter
import com.shivam.simplecalculator.domain.util.strategies.DiscountStrategy
import com.shivam.simplecalculator.domain.models.UnitOption
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class DiscountCalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityDiscountCalculatorBinding
    private var isTopFocused = true
    private var priceValue = ""
    private var discountValue = ""

    private val strategy = DiscountStrategy()
    private val defaultUnit = UnitOption("Unit", 1.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscountCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        updateFocus()
        updateDisplay()
        calculateResult()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.tvTopValue.showSoftInputOnFocus = false
        binding.tvBottomValue.showSoftInputOnFocus = false

        binding.cardTopInput.setOnClickListener {
            isTopFocused = true
            updateFocus()
        }

        binding.cardBottomInput.setOnClickListener {
            isTopFocused = false
            updateFocus()
        }

        val buttons = listOf(
            binding.numpad.btn0, binding.numpad.btn1, binding.numpad.btn2,
            binding.numpad.btn3, binding.numpad.btn4, binding.numpad.btn5,
            binding.numpad.btn6, binding.numpad.btn7, binding.numpad.btn8,
            binding.numpad.btn9, binding.numpad.btnDot
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                VibrationUtil.vibrate(this)
                appendChar(getButtonText(button))
            }
        }

        binding.numpad.btnAC.setOnClickListener {
            VibrationUtil.vibrate(this)
            priceValue = ""
            discountValue = ""
            updateDisplay()
            calculateResult()
        }

        binding.numpad.btnDel.setOnClickListener {
            VibrationUtil.vibrate(this)
            val et = if (isTopFocused) binding.tvTopValue else binding.tvBottomValue
            var currentVal = if (isTopFocused) priceValue else discountValue
            
            var start = et.selectionStart
            var end = et.selectionEnd

            if (start < 0) start = currentVal.length
            if (end < 0) end = currentVal.length

            val min = minOf(start, end)
            val max = maxOf(start, end)

            if (currentVal.isNotEmpty()) {
                val rawPos = ExpressionFormatter.getRawPosition(currentVal, min)
                if (min == max) {
                    if (rawPos > 0) {
                        val builder = StringBuilder(currentVal).deleteCharAt(rawPos - 1)
                        currentVal = builder.toString()
                        if (isTopFocused) priceValue = currentVal else discountValue = currentVal
                        updateDisplay()
                        val newFormattedPos = ExpressionFormatter.getFormattedPosition(currentVal, rawPos - 1)
                        et.setSelection(newFormattedPos)
                    }
                } else {
                    val rawEnd = ExpressionFormatter.getRawPosition(currentVal, max)
                    val builder = StringBuilder(currentVal).delete(rawPos, rawEnd)
                    currentVal = builder.toString()
                    if (isTopFocused) priceValue = currentVal else discountValue = currentVal
                    updateDisplay()
                    val newFormattedPos = ExpressionFormatter.getFormattedPosition(currentVal, rawPos)
                    et.setSelection(newFormattedPos)
                }
            }
            calculateResult()
        }

        binding.numpad.btnGo.setOnClickListener {
            VibrationUtil.vibrate(this)
            calculateResult()
        }
    }

    private fun appendChar(char: String) {
        val et = if (isTopFocused) binding.tvTopValue else binding.tvBottomValue
        var currentVal = if (isTopFocused) priceValue else discountValue

        var start = et.selectionStart
        var end = et.selectionEnd

        if (start < 0) start = currentVal.length
        if (end < 0) end = currentVal.length

        val min = minOf(start, end)
        val max = maxOf(start, end)
        val rawPos = ExpressionFormatter.getRawPosition(currentVal, min)
        val rawEnd = ExpressionFormatter.getRawPosition(currentVal, max)

        if (char == "." && currentVal.contains(".")) return
        if (currentVal.length - (rawEnd - rawPos) + char.length > 15) return

        val builder = java.lang.StringBuilder(currentVal)
        builder.replace(rawPos, rawEnd, char)
        val newVal = builder.toString()

        if (!isTopFocused) {
            val percentVal = newVal.toDoubleOrNull()
            if (percentVal != null && percentVal > 100.0) {
                android.widget.Toast.makeText(this, "Invalid percentage", android.widget.Toast.LENGTH_SHORT).show()
                return
            }
        }

        currentVal = newVal

        if (isTopFocused) priceValue = currentVal else discountValue = currentVal
        updateDisplay()

        val newFormattedPos = ExpressionFormatter.getFormattedPosition(currentVal, rawPos + char.length)
        if (newFormattedPos <= (et.text?.length ?: 0)) {
            et.setSelection(newFormattedPos)
        }
        calculateResult()
    }

    private fun updateFocus() {
        binding.cardTopInput.setCardBackgroundColor(if (isTopFocused) Color.parseColor("#E0E0E0") else Color.WHITE)
        binding.cardBottomInput.setCardBackgroundColor(if (!isTopFocused) Color.parseColor("#E0E0E0") else Color.WHITE)
        if (isTopFocused) {
            binding.tvTopValue.requestFocus()
        } else {
            binding.tvBottomValue.requestFocus()
        }
    }

    private fun updateDisplay() {
        val formattedTop = ExpressionFormatter.format(priceValue)
        val formattedBottom = ExpressionFormatter.format(discountValue)
        
        if (binding.tvTopValue.text.toString() != formattedTop) binding.tvTopValue.setText(formattedTop)
        if (binding.tvBottomValue.text.toString() != formattedBottom) binding.tvBottomValue.setText(formattedBottom)
    }

    private fun calculateResult() {
        if (priceValue.isEmpty() || discountValue.isEmpty() || priceValue == "." || discountValue == ".") {
            binding.tvResultValue.visibility = View.INVISIBLE
            return
        }

        val price = priceValue.toDoubleOrNull() ?: 0.0
        val discountPercent = discountValue.toDoubleOrNull() ?: 0.0

        if (price > 0 && discountPercent >= 0) {
            val result = strategy.convert(price, discountPercent, defaultUnit, defaultUnit)
            val discountAmount = price - result
            
            val decFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
            val discStr = if (discountAmount % 1.0 == 0.0) String.format(Locale.US, "%.0f", discountAmount) else decFormat.format(discountAmount)
            val finalStr = if (result % 1.0 == 0.0) String.format(Locale.US, "%.0f", result) else decFormat.format(result)
            
            val formattedDisc = ExpressionFormatter.formatNumberToken(discStr)
            val formattedFinal = ExpressionFormatter.formatNumberToken(finalStr)
            
            binding.tvResultValue.text = "Discount = ₹$formattedDisc\nFinal Price = ₹$formattedFinal"
            binding.tvResultValue.visibility = View.VISIBLE
        } else {
            binding.tvResultValue.visibility = View.INVISIBLE
        }
    }
}
