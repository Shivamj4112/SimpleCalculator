package com.shivam.simplecalculator

import android.os.Bundle
import android.widget.Button

import androidx.core.content.ContextCompat
import com.shivam.simplecalculator.databinding.ActivityGstCalculatorBinding
import java.util.Locale

class GstCalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityGstCalculatorBinding
    private var originalPrice = ""
    private var selectedGstRate = 12 // Default 12%

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

        // GST Rates
        val rateMap = mapOf(
            binding.btnGst3 to 3,
            binding.btnGst5 to 5,
            binding.btnGst12 to 12,
            binding.btnGst18 to 18,
            binding.btnGst28 to 28
        )

        rateMap.forEach { (button, rate) ->
            button.setOnClickListener {
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
                val char = (it as Button).text.toString()
                if (char == "." && originalPrice.contains(".")) return@setOnClickListener
                originalPrice += char
                updateDisplay()
            }
        }

        binding.numpad.btnAC.setOnClickListener {
            originalPrice = ""
            updateDisplay()
        }

        binding.numpad.btnDel.setOnClickListener {
            if (originalPrice.isNotEmpty()) {
                originalPrice = originalPrice.dropLast(1)
            }
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        binding.tvOriginalPrice.text = if (originalPrice.isEmpty()) "0" else originalPrice
        calculateGst()
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
                button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            } else {
                button.setBackgroundColor(Color.parseColor("#F0F0F0"))
                button.setTextColor(Color.parseColor("#333333"))
            }
        }
    }

    private fun calculateGst() {
        val price = originalPrice.toDoubleOrNull() ?: 0.0
        val gstTotal = price * selectedGstRate / 100.0
        val finalPrice = price + gstTotal
        val halfGst = gstTotal / 2.0

        binding.tvFinalPrice.text = String.format(Locale.US, "%.2f", finalPrice)
        binding.tvGstSummary.text = String.format(Locale.US, "CGST/SGST: %.2f", halfGst)
    }

    // Helper for non-context Color.parseColor
    private object Color {
        fun parseColor(colorString: String): Int = android.graphics.Color.parseColor(colorString)
    }
}
