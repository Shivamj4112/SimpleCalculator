package com.shivam.simplecalculator.ui.activites

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityConverterBinding
import com.shivam.simplecalculator.domain.models.ConverterConfig
import com.shivam.simplecalculator.domain.models.ConverterType
import com.shivam.simplecalculator.domain.models.UnitOption
import com.shivam.simplecalculator.domain.util.strategies.DateStrategy
import com.shivam.simplecalculator.domain.util.VibrationUtil
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale
import androidx.core.graphics.toColorInt

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
        // Set title based on type name
        val titleText = currentType.name.lowercase().replaceFirstChar { it.uppercase() }
        binding.tvConverterTitle.text = getString(R.string.title_converter, titleText)

        when (currentType) {
            ConverterType.BMI -> {
                binding.tvTopLabelText.text = "Weight"
                binding.tvBottomLabelText.text = "Height"
            }
            ConverterType.DISCOUNT -> {
                binding.tvTopLabelText.text = "Price"
                binding.tvBottomLabelText.text = "Discount %"
            }
            ConverterType.DATE -> {
                binding.tvTopLabelText.text = "Start Date"
                binding.tvBottomLabelText.text = "End Date"
                
                val calendar = Calendar.getInstance()
                val dateStr = String.format(Locale.US, "%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
                inputValue = dateStr
                outputValue = dateStr
                
                binding.ivTopDropdown.visibility = View.GONE
                binding.ivBottomDropdown.visibility = View.GONE
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
            if (currentType == ConverterType.DATE) {
                showDatePicker(true)
            } else if (currentType !in listOf(ConverterType.BMI, ConverterType.DISCOUNT)) {
                showUnitDialog(true)
            } else {
                isTopFocused = true
                updateFocus()
            }
        }

        binding.cardBottomInput.setOnClickListener {
            if (currentType == ConverterType.DATE) {
                showDatePicker(false)
            } else if (currentType !in listOf(ConverterType.BMI, ConverterType.DISCOUNT)) {
                showUnitDialog(false)
            } else {
                isTopFocused = false
                updateFocus()
            }
        }

        binding.llTopLabel.setOnClickListener { binding.cardTopInput.performClick() }
        binding.llTopValueBox.setOnClickListener { binding.cardTopInput.performClick() }
        binding.ivBottomDropdown.setOnClickListener { binding.cardBottomInput.performClick() }
        binding.linearLayout7.setOnClickListener { binding.cardBottomInput.performClick() }


        val numpad = binding.numpad
        val buttons = listOf(
            numpad.btn0, numpad.btn1, numpad.btn2, numpad.btn3,
            numpad.btn4, numpad.btn5, numpad.btn6, numpad.btn7,
            numpad.btn8, numpad.btn9, numpad.btnDot
        )

        if (currentType == ConverterType.DATE) {
            buttons.forEach { it.isEnabled = false }
            numpad.btnDel.isEnabled = false
            numpad.btnAC.isEnabled = false
            numpad.root.visibility = View.GONE
        } else {
            buttons.forEach { button ->
                button.setOnClickListener {
                    VibrationUtil.vibrate(this)
                    appendChar(getButtonText(it))
                }
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
                if (inputValue.isNotEmpty()) inputValue = inputValue.dropLast(1)
            } else {
                if (outputValue.isNotEmpty()) outputValue = outputValue.dropLast(1)
            }
            updateDisplay()
            performLiveCalculation()
        }

        numpad.btnGo.setOnClickListener {
            VibrationUtil.vibrate(this)
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
        performLiveCalculation()
    }

    private fun updateDisplay() {
        binding.tvTopValue.text = inputValue.ifEmpty { "0" }
        binding.tvBottomValue.text = outputValue.ifEmpty { "0" }
        binding.tvTopUnit.text = topUnit.name
        binding.tvBottomUnit.text = bottomUnit.name
    }

    private fun performLiveCalculation() {
        val isTwoInput = currentType in listOf(ConverterType.BMI, ConverterType.DISCOUNT, ConverterType.DATE)
        if (isTwoInput) {
            binding.tvResultLabel.visibility = View.INVISIBLE
            binding.tvResultValue.visibility = View.INVISIBLE
            binding.mcvDateResult.visibility = View.GONE
            binding.resultContainer.visibility = View.VISIBLE
            return
        }

        if (currentType == ConverterType.NUMERAL) {
            val strategy = ConverterConfig.getStrategy(currentType) as com.shivam.simplecalculator.domain.util.strategies.NumeralStrategy
            val resultStr = strategy.convertBase(inputValue, topUnit.factor.toInt(), bottomUnit.factor.toInt())
            outputValue = resultStr
            binding.tvBottomValue.text = outputValue.ifEmpty { "0" }

            if (inputValue.isEmpty() || inputValue == ".") {
                binding.tvResultLabel.visibility = View.INVISIBLE
                binding.tvResultValue.visibility = View.INVISIBLE
                return
            }

            val otherResults = StringBuilder()
            for (unit in availableUnits) {
                if (unit == topUnit) continue
                val otherRes = strategy.convertBase(inputValue, topUnit.factor.toInt(), unit.factor.toInt())
                otherResults.append("${unit.name} : $otherRes\n")
            }
            binding.tvResultValue.text = otherResults.toString().trim()
            binding.tvResultValue.setTextColor(getColor(R.color.text_color))
            binding.tvResultValue.visibility = View.VISIBLE
            binding.tvResultLabel.visibility = View.INVISIBLE
            binding.mcvDateResult.visibility = View.GONE
            binding.resultContainer.visibility = View.VISIBLE
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

        if (inputValue.isEmpty() || inputValue == ".") {
            binding.tvResultLabel.visibility = View.INVISIBLE
            binding.tvResultValue.visibility = View.INVISIBLE
            return
        }

        val otherResults = StringBuilder()
        for (unit in availableUnits) {
            if (unit == topUnit) continue
            val otherRes = strategy.convert(val1, 0.0, topUnit, unit)
            val formatRes = if (otherRes % 1.0 == 0.0) {
                String.format(java.util.Locale.US, "%.0f", otherRes)
            } else {
                decFormat.format(otherRes)
            }
            if (currentType == ConverterType.TEMPERATURE) {
                val unitSymbol = when(unit.name) {
                    "Celsius" -> "°C"
                    "Fahrenheit" -> "°F"
                    "Kelvin" -> "K"
                    else -> ""
                }
                otherResults.append("${unit.name} : $formatRes $unitSymbol\n")
            } else {
                otherResults.append("${unit.name} : $formatRes\n")
            }
        }

        binding.tvResultValue.text = otherResults.toString().trim()
        binding.tvResultValue.setTextColor(getColor(R.color.text_color))
        binding.tvResultValue.visibility = View.VISIBLE
        binding.tvResultLabel.visibility = View.INVISIBLE
        binding.mcvDateResult.visibility = View.GONE
        binding.resultContainer.visibility = View.VISIBLE
    }


    private fun updateFocus() {
        binding.cardTopInput.setCardBackgroundColor(if (isTopFocused) Color.parseColor("#E0E0E0") else Color.WHITE)
        binding.cardBottomInput.setCardBackgroundColor(if (!isTopFocused) Color.parseColor("#E0E0E0") else Color.WHITE)
    }

    private fun calculateResult() {
        if (inputValue.isEmpty() || outputValue.isEmpty() || inputValue == "." || outputValue == ".") {
            binding.tvResultLabel.visibility = View.INVISIBLE
            binding.tvResultValue.text = "Invalid input"
            binding.tvResultValue.setTextColor(Color.RED)
            binding.tvResultValue.visibility = View.VISIBLE
            return
        }

        if (currentType == ConverterType.DATE) {
            val strategy = ConverterConfig.getStrategy(currentType) as DateStrategy
            val result = strategy.calculateDetailedDiff(inputValue, outputValue)

            if (result != null) {
                binding.mcvDateResult.visibility = View.VISIBLE
                binding.resultContainer.visibility = View.GONE
                
                binding.tvTotalDaysNumber.text = result.totalDays.toString()
                binding.tvResYears.text = result.years.toString()
                binding.tvResMonths.text = result.months.toString()
                binding.tvResDays.text = result.days.toString()
                binding.tvResWeeks.text = "Weeks\n${result.weeks}"
                binding.tvResHours.text = "Hours\n${result.hours}"
                binding.tvResMinutes.text = "Minutes\n${result.minutes}"
            } else {
                binding.tvResultValue.text = "Invalid Date Range"
                binding.tvResultValue.setTextColor(Color.RED)
                binding.tvResultValue.visibility = View.VISIBLE
                binding.resultContainer.visibility = View.VISIBLE
                binding.mcvDateResult.visibility = View.GONE
            }
            return
        }

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
        } else if (currentType == ConverterType.DISCOUNT) {
            val discountAmount = val1 - result
            val decFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
            val discStr = if (discountAmount % 1.0 == 0.0) String.format(Locale.US, "%.0f", discountAmount) else decFormat.format(discountAmount)
            val finalStr = if (result % 1.0 == 0.0) String.format(Locale.US, "%.0f", result) else decFormat.format(result)
            
            binding.tvResultValue.text = "Discount = ₹$discStr\nFinal Price = ₹$finalStr"
            binding.tvResultValue.setTextColor(Color.parseColor("#38B000")) 
            
            binding.tvResultValue.visibility = View.VISIBLE
            binding.tvResultLabel.visibility = View.INVISIBLE
        } else {
            performLiveCalculation()
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

    private fun showDatePicker(isTop: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_picker, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

        val title = dialogView.findViewById<TextView>(R.id.tvPickerTitle)
        val npDay = dialogView.findViewById<com.shivam.simplecalculator.domain.util.CustomNumberPicker>(R.id.npDay)
        val npMonth = dialogView.findViewById<com.shivam.simplecalculator.domain.util.CustomNumberPicker>(R.id.npMonth)
        val npYear = dialogView.findViewById<com.shivam.simplecalculator.domain.util.CustomNumberPicker>(R.id.npYear)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)
        val btnClose = dialogView.findViewById<ImageView>(R.id.btnClosePicker)

        title.text = if (isTop) "Start Date" else "End Date"
        
        val currentStr = if (isTop) inputValue else outputValue
        val calendar = Calendar.getInstance()
        if (currentStr.isNotEmpty()) {
            try {
                val parts = currentStr.split("/")
                calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
                calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                calendar.set(Calendar.YEAR, parts[2].toInt())
            } catch (e: Exception) {}
        }

        npYear.minValue = 1900
        npYear.maxValue = 2100
        npYear.value = calendar.get(Calendar.YEAR)

        val monthsArray = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        npMonth.minValue = 0
        npMonth.maxValue = 11
        npMonth.displayedValues = monthsArray
        npMonth.value = calendar.get(Calendar.MONTH)

        npDay.minValue = 1
        npDay.maxValue = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        npDay.value = calendar.get(Calendar.DAY_OF_MONTH)

        val updateDays = {
            val tempCal = Calendar.getInstance()
            tempCal.set(npYear.value, npMonth.value, 1)
            npDay.maxValue = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        npMonth.setOnValueChangedListener { _, _, _ -> updateDays() }
        npYear.setOnValueChangedListener { _, _, _ -> updateDays() }

        btnOk.setOnClickListener {
            val dateStr = String.format(Locale.US, "%02d/%02d/%04d", npDay.value, npMonth.value + 1, npYear.value)
            if (isTop) {
                inputValue = dateStr
            } else {
                outputValue = dateStr
            }
            updateDisplay()
            calculateResult()
            dialog.dismiss()
        }
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
