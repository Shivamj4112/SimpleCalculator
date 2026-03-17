package com.shivam.simplecalculator

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
import com.shivam.simplecalculator.databinding.ActivityConverterBinding
import com.shivam.simplecalculator.models.ConverterConfig
import com.shivam.simplecalculator.models.ConverterType
import com.shivam.simplecalculator.models.UnitOption
import java.util.Locale

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
    }

    private fun setupInitialState() {
        // Set title based on type name
        val titleText = currentType.name.lowercase().replaceFirstChar { it.uppercase() }
        binding.tvConverterTitle.text = titleText

        when (currentType) {
            ConverterType.BMI -> {
                binding.tvTopLabelText.text = "Weight"
                binding.tvBottomLabelText.text = "Height"
            }
            ConverterType.DISCOUNT -> {
                binding.tvTopLabelText.text = "Price"
                binding.tvBottomLabelText.text = "Discount %"
            }
            else -> {
                binding.tvTopLabelText.text = "From"
                binding.tvBottomLabelText.text = "To"
            }
        }

        availableUnits = ConverterConfig.unitMap[currentType] ?: emptyList()
        
        if (availableUnits.isNotEmpty()) {
            topUnit = availableUnits[0]
            bottomUnit = if (availableUnits.size > 1) availableUnits[1] else availableUnits[0]
            
            if (currentType == ConverterType.BMI) {
                topUnit = availableUnits.find { it.name == "Kilograms" } ?: availableUnits[0]
                bottomUnit = availableUnits.find { it.name == "Centimeters" } ?: availableUnits[0]
            }
        } else {
            topUnit = UnitOption("Unit", 1.0)
            bottomUnit = UnitOption("Unit", 1.0)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cardTopInput.setOnClickListener {
            isTopFocused = true
            updateFocus()
        }

        binding.cardBottomInput.setOnClickListener {
            isTopFocused = false
            updateFocus()
        }

        binding.ivTopDropdown.setOnClickListener { showUnitDialog(true) }
        binding.ivBottomDropdown.setOnClickListener { showUnitDialog(false) }

        val numpad = binding.numpad
        val buttons = listOf(
            numpad.btn0, numpad.btn1, numpad.btn2, numpad.btn3,
            numpad.btn4, numpad.btn5, numpad.btn6, numpad.btn7,
            numpad.btn8, numpad.btn9, numpad.btnDot
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                appendChar(getButtonText(it))
            }
        }

        numpad.btnAC.setOnClickListener {
            inputValue = ""
            outputValue = ""
            updateDisplay()
        }

        numpad.btnDel.setOnClickListener {
            if (isTopFocused) {
                if (inputValue.isNotEmpty()) inputValue = inputValue.dropLast(1)
            } else {
                if (outputValue.isNotEmpty()) outputValue = outputValue.dropLast(1)
            }
            updateDisplay()
        }

        numpad.btnGo.setOnClickListener {
            calculateResult()
        }
    }

    private fun appendChar(char: String) {
        if (isTopFocused) {
            if (char == "." && inputValue.contains(".")) return
            inputValue += char
        } else {
            if (char == "." && outputValue.contains(".")) return
            outputValue += char
        }
        updateDisplay()
    }

    private fun updateDisplay() {
        binding.tvTopValue.text = inputValue.ifEmpty { "0" }
        binding.tvBottomValue.text = outputValue.ifEmpty { "0" }
        binding.tvTopUnit.text = topUnit.name
        binding.tvBottomUnit.text = bottomUnit.name

        binding.tvResultLabel.visibility = View.INVISIBLE
        binding.tvResultValue.visibility = View.INVISIBLE
    }

    private fun updateFocus() {
        binding.cardTopInput.setCardBackgroundColor(if (isTopFocused) Color.parseColor("#E0E0E0") else Color.WHITE)
        binding.cardBottomInput.setCardBackgroundColor(if (!isTopFocused) Color.parseColor("#E0E0E0") else Color.WHITE)
    }

    private fun calculateResult() {
        val val1 = inputValue.toDoubleOrNull() ?: 0.0
        val val2 = outputValue.toDoubleOrNull() ?: 0.0
        
        val strategy = ConverterConfig.getStrategy(currentType)
        val result = strategy.convert(val1, val2, topUnit, bottomUnit)

        if (currentType == ConverterType.BMI) {
            if (val1 > 0 && val2 > 0) {
                val roundedResult = String.format(Locale.US, "%.1f", result)
                val (category, color) = getBmiCategory(result)

                binding.tvResultValue.text = roundedResult
                binding.tvResultLabel.text = category
                
                binding.tvResultValue.setTextColor(color)
                binding.tvResultLabel.setTextColor(color)

                binding.tvResultLabel.visibility = View.VISIBLE
                binding.tvResultValue.visibility = View.VISIBLE
            }
        } else {
            val formattedResult = if (result % 1.0 == 0.0) {
                String.format(Locale.US, "%.0f", result)
            } else {
                String.format(Locale.US, "%.2f", result)
            }
            
            binding.tvResultValue.text = formattedResult
            binding.tvResultValue.setTextColor(Color.parseColor("#38B000")) 
            
            binding.tvResultValue.visibility = View.VISIBLE
            binding.tvResultLabel.visibility = View.INVISIBLE
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
        title.text = if (isTop) binding.tvTopLabelText.text else binding.tvBottomLabelText.text

        val optionContainer = dialogView.findViewById<LinearLayout>(R.id.optionContainer)
        optionContainer.removeAllViews()

        val activeUnit = if (isTop) topUnit else bottomUnit

        var unitsToShow = availableUnits
        
        // Special logic to partition BMI options if needed
        if (currentType == ConverterType.BMI) {
            unitsToShow = if (isTop) {
                availableUnits.filter { it.name == "Kilograms" || it.name == "Pounds" }
            } else {
                availableUnits.filter { it.name == "Centimeters" || it.name == "Inches" }
            }
        }

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
