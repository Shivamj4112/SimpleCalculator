package com.shivam.simplecalculator.ui.activites

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityGstCalculatorBinding
import com.shivam.simplecalculator.domain.util.ExpressionFormatter
import com.shivam.simplecalculator.domain.util.VibrationUtil
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

class GstCalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityGstCalculatorBinding
    private var originalPrice = ""
    private var selectedGstRate = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGstCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        updateGstSelection()
        calculateGst()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        val rateMap = mapOf(
            binding.btnGst3 to 3,
            binding.btnGst5 to 5,
            binding.btnGst12 to 12,
            binding.btnGst18 to 18,
            binding.btnGst28 to 28
        )

        rateMap.forEach { (button, rate) ->
            button.setOnClickListener {
                VibrationUtil.vibrate(this)
                selectedGstRate = rate
                updateGstSelection()
                calculateGst()
            }
        }

        // Numpad
        val buttons = listOf(
            binding.numpad.btn0, binding.numpad.btn1, binding.numpad.btn2, binding.numpad.btn3,
            binding.numpad.btn4, binding.numpad.btn5, binding.numpad.btn6, binding.numpad.btn7,
            binding.numpad.btn8, binding.numpad.btn9, binding.numpad.btnDot
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                VibrationUtil.vibrate(this)
                val char = getButtonText(it)
                if (char == "." && originalPrice.contains(".")) return@setOnClickListener
                if (originalPrice.length >= 11) return@setOnClickListener
                originalPrice += char
                updateDisplay()
            }
        }

        binding.numpad.btnAC.setOnClickListener {
            VibrationUtil.vibrate(this)
            originalPrice = ""
            updateDisplay()
        }

        binding.numpad.btnDel.setOnClickListener {
            VibrationUtil.vibrate(this)
            if (originalPrice.isNotEmpty()) {
                originalPrice = originalPrice.dropLast(1)
            }
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        binding.tvOriginalPrice.text = ExpressionFormatter.format(originalPrice).ifEmpty { "0" }
        calculateGst()
    }

    private fun formatValue(valueStr: String): String {
        if (valueStr.isEmpty() || valueStr == ".") return valueStr.ifEmpty { "0" }
        
        return ExpressionFormatter.formatNumberToken(valueStr)
    }

    private fun updateGstSelection() {
        val buttons = listOf(
            binding.btnGst3, binding.btnGst5, binding.btnGst12,
            binding.btnGst18, binding.btnGst28
        )
        val rateMap = mapOf(
            binding.btnGst3 to 3,
            binding.btnGst5 to 5,
            binding.btnGst12 to 12,
            binding.btnGst18 to 18,
            binding.btnGst28 to 28
        )

        buttons.forEach { button ->
            if (rateMap[button] == selectedGstRate) {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColor))
                button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            } else {
                button.setBackgroundColor(Color.parseColor("#F0F0F0"))
                button.setTextColor(Color.parseColor("#333333"))
            }
        }
    }

    private fun calculateGst() {
        if (originalPrice.isEmpty() || originalPrice == ".") {
            binding.tvFinalPrice.text = "0.00"
            binding.tvGstSummary.text = "CGST/SGST\n0.00"
            return
        }

        try {
            val price = BigDecimal(originalPrice)
            val gstRate = BigDecimal(selectedGstRate.toString())
            val hundred = BigDecimal("100")
            
            val gstTotal = price.multiply(gstRate).divide(hundred, 2, RoundingMode.HALF_UP)
            val finalPrice = price.add(gstTotal)
            val halfGst = gstTotal.divide(BigDecimal("2"), 2, RoundingMode.HALF_UP)

            binding.tvFinalPrice.text = ExpressionFormatter.formatNumberToken(String.format(Locale.US, "%.2f", finalPrice.toDouble()))
            binding.tvGstSummary.text = String.format(Locale.US, "CGST/SGST\n%s", formatValue(halfGst.toString()))
        } catch (e: Exception) {
            binding.tvFinalPrice.text = "Error"
            binding.tvGstSummary.text = "CGST/SGST\n0.00"
        }
    }

    private object Color {
        fun parseColor(colorString: String): Int = colorString.toColorInt()
    }
}
