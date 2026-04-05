package com.shivam.simplecalculator.ui.activites

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityBmiCalculatorBinding
import com.shivam.simplecalculator.domain.models.UnitOption
import com.shivam.simplecalculator.domain.util.ExpressionFormatter
import com.shivam.simplecalculator.domain.util.VibrationUtil
import com.shivam.simplecalculator.domain.util.strategies.BmiStrategy
import java.util.Locale

class BmiCalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityBmiCalculatorBinding
    private var isTopFocused = true
    private var topValue = ""
    private var bottomValue = ""

    private val weightUnits = listOf(
        UnitOption("Kilograms", 1.0),
        UnitOption("Pounds", 0.453592)
    )

    private val heightUnits = listOf(
        UnitOption("Centimeters", 0.01),
        UnitOption("Inches", 0.0254)
    )

    private var topUnit = weightUnits[0]
    private var bottomUnit = heightUnits[0]
    private val strategy = BmiStrategy()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiCalculatorBinding.inflate(layoutInflater)
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

        binding.llTopLabel.setOnClickListener { showUnitDialog(true) }
        binding.llBottomLabel.setOnClickListener { showUnitDialog(false) }

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
            topValue = ""
            bottomValue = ""
            updateDisplay()
            calculateResult()
        }

        binding.numpad.btnDel.setOnClickListener {
            VibrationUtil.vibrate(this)
            val et = if (isTopFocused) binding.tvTopValue else binding.tvBottomValue
            var currentVal = if (isTopFocused) topValue else bottomValue
            
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
                        if (isTopFocused) topValue = currentVal else bottomValue = currentVal
                        updateDisplay()
                        val newFormattedPos = ExpressionFormatter.getFormattedPosition(currentVal, rawPos - 1)
                        et.setSelection(newFormattedPos)
                    }
                } else {
                    val rawEnd = ExpressionFormatter.getRawPosition(currentVal, max)
                    val builder = StringBuilder(currentVal).delete(rawPos, rawEnd)
                    currentVal = builder.toString()
                    if (isTopFocused) topValue = currentVal else bottomValue = currentVal
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
        var currentVal = if (isTopFocused) topValue else bottomValue

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
        currentVal = builder.toString()

        if (isTopFocused) topValue = currentVal else bottomValue = currentVal
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
        val formattedTop = ExpressionFormatter.format(topValue)
        val formattedBottom = ExpressionFormatter.format(bottomValue)
        
        if (binding.tvTopValue.text.toString() != formattedTop) binding.tvTopValue.setText(formattedTop)
        if (binding.tvBottomValue.text.toString() != formattedBottom) binding.tvBottomValue.setText(formattedBottom)
        
        binding.tvTopUnit.text = topUnit.name
        binding.tvBottomUnit.text = bottomUnit.name
    }

    private fun calculateResult() {
        if (topValue.isEmpty() || bottomValue.isEmpty() || topValue == "." || bottomValue == ".") {
            binding.tvResultLabel.visibility = View.INVISIBLE
            binding.tvResultValue.visibility = View.INVISIBLE
            return
        }

        val weight = topValue.toDoubleOrNull() ?: 0.0
        val height = bottomValue.toDoubleOrNull() ?: 0.0
        
        if (weight > 0 && height > 0) {
            val result = strategy.convert(weight, height, topUnit, bottomUnit)
            val roundedResult = ExpressionFormatter.formatNumberToken(String.format(Locale.US, "%.1f", result))
            val (category, color) = getBmiCategory(result)

            binding.tvResultValue.text = roundedResult
            binding.tvResultLabel.text = category
            
            binding.tvResultValue.setTextColor(color)
            binding.tvResultLabel.setTextColor(color)

            binding.tvResultLabel.visibility = View.VISIBLE
            binding.tvResultValue.visibility = View.VISIBLE
        } else {
            binding.tvResultLabel.visibility = View.INVISIBLE
            binding.tvResultValue.visibility = View.INVISIBLE
        }
    }

    private fun getBmiCategory(bmi: Double): Pair<String, Int> {
        return when {
            bmi < 16.0 -> "Severely Underweight ⚠️" to Color.parseColor("#2196F3")
            bmi < 18.5 -> "Underweight" to Color.parseColor("#2196F3")
            bmi < 25.0 -> "Normal (Healthy) ✅" to Color.parseColor("#4CAF50")
            bmi < 30.0 -> "Overweight" to Color.parseColor("#FF9800")
            bmi < 35.0 -> "Obese (Class 1) ⚠️" to Color.parseColor("#F44336")
            bmi < 40.0 -> "Obese (Class 2) ⚠️" to Color.parseColor("#F44336")
            else -> "Severely Obese (Class 3) 🚨" to Color.parseColor("#D32F2F")
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
        title.text = if (isTop) "Weight" else "Height"

        val optionContainer = dialogView.findViewById<LinearLayout>(R.id.optionContainer)
        optionContainer.removeAllViews()

        val activeUnit = if (isTop) topUnit else bottomUnit
        val unitsToShow = if (isTop) weightUnits else heightUnits

        unitsToShow.forEach { unit ->
            val itemView = layoutInflater.inflate(R.layout.item_unit_selection, optionContainer, false)
            val ivIcon = itemView.findViewById<ImageView>(R.id.ivOptionIcon)
            val tvText = itemView.findViewById<TextView>(R.id.tvOptionText)

            tvText.text = unit.name
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
