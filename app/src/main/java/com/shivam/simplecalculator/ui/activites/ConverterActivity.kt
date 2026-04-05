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
import com.shivam.simplecalculator.databinding.ActivityConverterBinding
import com.shivam.simplecalculator.domain.models.ConverterConfig
import com.shivam.simplecalculator.domain.models.ConverterType
import com.shivam.simplecalculator.domain.models.UnitOption
import com.shivam.simplecalculator.domain.util.ExpressionFormatter
import com.shivam.simplecalculator.domain.util.VibrationUtil

class ConverterActivity : BaseActivity() {

    companion object {
        const val EXTRA_TYPE = "converter_type"
    }

    private lateinit var binding: ActivityConverterBinding
    private var isTopFocused = true
    private var inputValue = ""
    private var outputValue = ""
    
    private lateinit var currentType: ConverterType
    private lateinit var topUnit: UnitOption
    private lateinit var bottomUnit: UnitOption
    private var availableUnits: List<UnitOption> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeString = intent.getStringExtra(EXTRA_TYPE) ?: ConverterType.LENGTH.name
        currentType = try { ConverterType.valueOf(typeString) } catch (e: Exception) { ConverterType.LENGTH }

        setupInitialState()
        setupListeners()
        updateFocus()
        updateDisplay()
        calculateResult()
    }

    private fun setupInitialState() {
        val titleRes = when (currentType) {
            ConverterType.LENGTH -> R.string.length_converter
            ConverterType.MASS -> R.string.mass_converter
            ConverterType.NUMERAL -> R.string.numeral_converter
            ConverterType.SPEED -> R.string.speed_converter
            ConverterType.TEMPERATURE -> R.string.temperature_converter
            ConverterType.TIME -> R.string.time_converter
            ConverterType.VOLUME -> R.string.volume_converter
            ConverterType.DATA -> R.string.data_converter
        }
        binding.tvConverterTitle.text = getString(titleRes)

        binding.tvTopLabelText.text = getString(R.string.from)
        binding.tvBottomLabelText.text = getString(R.string.to)

        availableUnits = ConverterConfig.unitMap[currentType] ?: emptyList()
        
        if (availableUnits.isNotEmpty()) {
            topUnit = availableUnits[0]
            bottomUnit = if (availableUnits.size > 1) availableUnits[1] else availableUnits[0]
        } else {
            topUnit = UnitOption("Unit", 1.0)
            bottomUnit = UnitOption("Unit", 1.0)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.tvTopValue.showSoftInputOnFocus = false
        binding.cardTopInput.setOnClickListener {
            isTopFocused = true
            updateFocus()
            binding.tvTopValue.requestFocus()
        }


        binding.llTopLabel.setOnClickListener { 
            showUnitDialog(true)
        }

        binding.llBottomLabel.setOnClickListener {
            showUnitDialog(false)
        }


        val numpad = binding.numpad
        val buttons = listOf(
            numpad.btn0, numpad.btn1, numpad.btn2, numpad.btn3,
            numpad.btn4, numpad.btn5, numpad.btn6, numpad.btn7,
            numpad.btn8, numpad.btn9, numpad.btnDot
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                VibrationUtil.vibrate(this)
                appendChar(getButtonText(it))
            }
        }

        numpad.btnAC.setOnClickListener {
            VibrationUtil.vibrate(this)
            inputValue = ""
            outputValue = ""
            updateDisplay()
            performLiveCalculation()
        }

        numpad.btnDel.setOnClickListener {
            VibrationUtil.vibrate(this)
            if (isTopFocused) {
                val et = binding.tvTopValue
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
                }
            } else {
                if (outputValue.isNotEmpty()) outputValue = outputValue.dropLast(1)
                updateDisplay()
            }
            performLiveCalculation()
        }

        numpad.btnGo.setOnClickListener {
            VibrationUtil.vibrate(this)
            calculateResult()
        }
    }

    private fun appendChar(char: String) {
        if (isTopFocused) {
            val et = binding.tvTopValue
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
            if (newFormattedPos <= (binding.tvTopValue.text?.length ?: 0)) {
                et.setSelection(newFormattedPos)
            }
        } else {
            if (char == "." && outputValue.contains(".")) return
            if (outputValue.length + char.length > 10) return
            outputValue += char
            updateDisplay()
        }
        performLiveCalculation()
    }

    private fun updateDisplay() {
        val formattedInput = ExpressionFormatter.format(inputValue)
        if (binding.tvTopValue.text.toString() != formattedInput) {
            binding.tvTopValue.setText(formattedInput)
        }
        
        val displayOutput = if (outputValue.isEmpty()) "0" else {
            if (currentType == ConverterType.NUMERAL) outputValue else ExpressionFormatter.formatNumberToken(outputValue)
        }
        binding.tvBottomValue.text = displayOutput
        
        binding.tvTopUnit.text = topUnit.name
        binding.tvBottomUnit.text = bottomUnit.name
        updateKeypadState()
    }

    private fun updateKeypadState() {
        val numpad = binding.numpad
        val digitButtons = listOf(
            numpad.btn0, numpad.btn1, numpad.btn2, numpad.btn3,
            numpad.btn4, numpad.btn5, numpad.btn6, numpad.btn7,
            numpad.btn8, numpad.btn9
        )
        
        var maxAllowedDigit = 9
        var allowDot = true
        
        if (currentType == ConverterType.NUMERAL) {
            allowDot = false
            when (topUnit.name) {
                "Binary" -> maxAllowedDigit = 1
                "Octal" -> maxAllowedDigit = 7
                "Decimal", "Hexadecimal" -> maxAllowedDigit = 9
            }
        }
        
        digitButtons.forEachIndexed { index, button ->
            val isEnabled = index <= maxAllowedDigit
            button.isEnabled = isEnabled
            button.alpha = if (isEnabled) 1.0f else 0.3f
        }
        
        numpad.btnDot.isEnabled = allowDot
        numpad.btnDot.alpha = if (allowDot) 1.0f else 0.3f
    }

    private fun performLiveCalculation() {


        if (inputValue.isEmpty() || inputValue == ".") {
            binding.tvResultValue.text = getString(R.string.invalid)
            binding.tvResultValue.setTextColor(Color.RED)
            binding.tvResultValue.visibility = View.VISIBLE
            binding.tvResultLabel.visibility = View.GONE
            binding.resultContainer.visibility = View.VISIBLE
            binding.tvBottomValue.text = "0"
            return
        }

        if (currentType == ConverterType.NUMERAL) {
            val strategy = ConverterConfig.getStrategy(currentType) as com.shivam.simplecalculator.domain.util.strategies.NumeralStrategy
            try {
                val resultValue = strategy.convertBase(inputValue, topUnit.factor.toInt(), bottomUnit.factor.toInt())
                outputValue = resultValue
                binding.tvBottomValue.text = outputValue.ifEmpty { "0" }

                binding.resultContainer.visibility = View.GONE
            } catch (e: Exception) {
                binding.tvResultValue.text = getString(R.string.invalid)
                binding.tvResultValue.setTextColor(Color.RED)
                binding.tvResultValue.visibility = View.VISIBLE
                binding.tvResultLabel.visibility = View.GONE
                binding.resultContainer.visibility = View.VISIBLE
            }
            return
        }

        val val1 = inputValue.toDoubleOrNull() ?: 0.0
        val strategy = ConverterConfig.getStrategy(currentType)
        val result = strategy.convert(val1, 0.0, topUnit, bottomUnit)

        val decFormat = java.text.DecimalFormat("#.######", java.text.DecimalFormatSymbols(java.util.Locale.US))
        outputValue = if (result % 1.0 == 0.0) {
            String.format(java.util.Locale.US, "%.0f", result)
        } else {
            decFormat.format(result)
        }
        binding.tvBottomValue.text = outputValue.ifEmpty { "0" }

        binding.resultContainer.visibility = View.GONE
    }


    private fun updateFocus() {
        if (isTopFocused) {
            binding.tvTopValue.requestFocus()
        }
    }

    private fun calculateResult() {
        if (inputValue.isEmpty() || outputValue.isEmpty() || inputValue == "." || outputValue == ".") {
            binding.tvResultLabel.visibility = View.INVISIBLE
            binding.tvResultValue.text = getString(R.string.invalid_input)
            binding.tvResultValue.setTextColor(Color.RED)
            binding.tvResultValue.visibility = View.VISIBLE
            return
        }



        val val1 = inputValue.toDoubleOrNull() ?: 0.0
        val val2 = outputValue.toDoubleOrNull() ?: 0.0
        
        val strategy = ConverterConfig.getStrategy(currentType)
        strategy.convert(val1, val2, topUnit, bottomUnit)

        performLiveCalculation()
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
        title.text = if (isTop) binding.tvTopLabelText.text else binding.tvBottomLabelText.text

        val optionContainer = dialogView.findViewById<LinearLayout>(R.id.optionContainer)
        optionContainer.removeAllViews()

        val activeUnit = if (isTop) topUnit else bottomUnit

        var unitsToShow = availableUnits
        

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
                performLiveCalculation()
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
