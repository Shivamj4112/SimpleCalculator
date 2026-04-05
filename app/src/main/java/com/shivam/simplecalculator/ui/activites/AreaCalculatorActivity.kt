package com.shivam.simplecalculator.ui.activites

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityAreaCalculatorBinding
import com.shivam.simplecalculator.domain.util.ExpressionFormatter
import com.shivam.simplecalculator.domain.util.VibrationUtil
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class AreaCalculatorActivity : BaseActivity() {

    data class AreaUnit(val name: String, val symbol: String, val factor: BigDecimal)

    private val areaUnits = listOf(
        AreaUnit("Square meter", "m²", BigDecimal("1.0")),
        AreaUnit("Square kilometre", "km²", BigDecimal("1000000.0")),
        AreaUnit("Square centimetre", "cm²", BigDecimal("0.0001")),
        AreaUnit("Square millimetre", "mm²", BigDecimal("0.000001")),
        AreaUnit("Acre", "ac", BigDecimal("4046.856422")),
        AreaUnit("Hectare", "ha", BigDecimal("10000.0")),
        AreaUnit("Square mile", "mi²", BigDecimal("2589988.11")),
        AreaUnit("Square yard", "yd²", BigDecimal("0.83612736")),
        AreaUnit("Square foot", "ft²", BigDecimal("0.09290304")),
        AreaUnit("Square inch", "in²", BigDecimal("0.00064516"))
    )

    private lateinit var binding: ActivityAreaCalculatorBinding
    private var inputValue = ""
    private lateinit var topUnit: AreaUnit
    private lateinit var bottomUnit: AreaUnit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAreaCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topUnit = areaUnits[0]
        bottomUnit = areaUnits[2]

        setupInitialState()
        setupListeners()
        updateDisplay()
        calculateResult()
    }

    private fun setupInitialState() {
        binding.tvValue1.showSoftInputOnFocus = false
        binding.cardInput1.setCardBackgroundColor(Color.parseColor("#E0E0E0"))
        binding.cardInput2.setCardBackgroundColor(Color.WHITE)
        binding.tvValue1.requestFocus()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cardInput1.setOnClickListener {
            binding.tvValue1.requestFocus()
        }

        binding.llLabel1.setOnClickListener { showUnitDialog(true) }
        binding.llLabel2.setOnClickListener { showUnitDialog(false) }

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
            inputValue = ""
            updateDisplay()
            calculateResult()
        }

        binding.numpad.btnDel.setOnClickListener {
            VibrationUtil.vibrate(this)
            val et = binding.tvValue1
            var start = et.selectionStart
            var end = et.selectionEnd

            if (start < 0) start = inputValue.length
            if (end < 0) end = inputValue.length

            val min = minOf(start, end)
            val max = maxOf(start, end)

            if (inputValue.isNotEmpty()) {
                val rawPos = ExpressionFormatter.getRawPosition(inputValue, min)
                if (min == max) {
                    if (rawPos > 0) {
                        val builder = java.lang.StringBuilder(inputValue)
                        builder.deleteCharAt(rawPos - 1)
                        inputValue = builder.toString()
                        updateDisplay()
                        val newFormattedPos = ExpressionFormatter.getFormattedPosition(inputValue, rawPos - 1)
                        et.setSelection(newFormattedPos)
                    }
                } else {
                    val rawEnd = ExpressionFormatter.getRawPosition(inputValue, max)
                    val builder = java.lang.StringBuilder(inputValue)
                    builder.delete(rawPos, rawEnd)
                    inputValue = builder.toString()
                    updateDisplay()
                    val newFormattedPos = ExpressionFormatter.getFormattedPosition(inputValue, rawPos)
                    et.setSelection(newFormattedPos)
                }
                calculateResult()
            }
        }
    }

    private fun appendChar(char: String) {
        val et = binding.tvValue1
        var start = et.selectionStart
        var end = et.selectionEnd

        if (start < 0) start = inputValue.length
        if (end < 0) end = inputValue.length

        val min = minOf(start, end)
        val max = maxOf(start, end)
        val rawPos = ExpressionFormatter.getRawPosition(inputValue, min)
        val rawEnd = ExpressionFormatter.getRawPosition(inputValue, max)

        if (char == "." && inputValue.contains(".")) return
        if (inputValue.length - (rawEnd - rawPos) + char.length > 15) return

        val builder = java.lang.StringBuilder(inputValue)
        builder.replace(rawPos, rawEnd, char)
        inputValue = builder.toString()

        updateDisplay()

        val newFormattedPos = ExpressionFormatter.getFormattedPosition(inputValue, rawPos + char.length)
        if (newFormattedPos <= (binding.tvValue1.text?.length ?: 0)) {
            et.setSelection(newFormattedPos)
        }
        calculateResult()
    }

    private fun updateDisplay() {
        val formattedInput = ExpressionFormatter.format(inputValue)
        if (binding.tvValue1.text.toString() != formattedInput) {
            binding.tvValue1.setText(formattedInput)
        }
        binding.tvUnitName1.text = topUnit.name
        binding.tvUnitSymbol1.text = topUnit.symbol
        
        binding.tvUnitName2.text = bottomUnit.name
        binding.tvUnitSymbol2.text = bottomUnit.symbol
    }

    private fun calculateResult() {
        if (inputValue.isEmpty() || inputValue == ".") {
            binding.tvValue2.text = "0"
            return
        }

        try {
            val value = BigDecimal(inputValue)
            
            val inSquareMeters = value.multiply(topUnit.factor)
            val result = inSquareMeters.divide(bottomUnit.factor, 8, RoundingMode.HALF_UP).stripTrailingZeros()
            
            val resultD = result.toDouble()
            val decFormat = DecimalFormat("#.######", DecimalFormatSymbols(Locale.US))
            
            val formattedResult = if (resultD % 1.0 == 0.0) {
                ExpressionFormatter.formatNumberToken(String.format(Locale.US, "%.0f", resultD))
            } else {
                ExpressionFormatter.formatNumberToken(decFormat.format(resultD))
            }
            
            binding.tvValue2.text = formattedResult
        } catch (e: Exception) {
            binding.tvValue2.text = "Error"
        }
    }

    private fun showUnitDialog(isTop: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_unit_selection, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val title = dialogView.findViewById<TextView>(R.id.tvSelectionTitle)
        title.text = if (isTop) "From" else "To"

        val optionContainer = dialogView.findViewById<LinearLayout>(R.id.optionContainer)
        optionContainer.removeAllViews()

        val activeUnit = if (isTop) topUnit else bottomUnit

        areaUnits.forEach { unit ->
            val itemView = layoutInflater.inflate(R.layout.item_unit_selection, optionContainer, false)
            val ivIcon = itemView.findViewById<ImageView>(R.id.ivOptionIcon)
            val tvText = itemView.findViewById<TextView>(R.id.tvOptionText)

            tvText.text = "${unit.name} (${unit.symbol})"
            val isSelected = (unit.name == activeUnit.name)
            ivIcon.setImageResource(if (isSelected) R.drawable.ic_checked else R.drawable.ic_unchecked)

            itemView.setOnClickListener {
                if (isTop) topUnit = unit else bottomUnit = unit
                updateDisplay()
                calculateResult()
                dialog.dismiss()
            }
            optionContainer.addView(itemView)
        }

        dialogView.findViewById<ImageView>(R.id.btnCloseSelection).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
