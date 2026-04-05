package com.shivam.simplecalculator.ui.activites

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityDateCalculatorBinding
import com.shivam.simplecalculator.domain.util.ExpressionFormatter
import com.shivam.simplecalculator.domain.util.strategies.DateStrategy
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

class DateCalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityDateCalculatorBinding
    private var startDate = ""
    private var endDate = ""

    private val strategy = DateStrategy()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDateCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = Calendar.getInstance()
        val dateStr = String.format(Locale.US, "%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
        startDate = dateStr
        endDate = dateStr

        setupListeners()
        updateDisplay()
        calculateResult()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cardTopInput.setOnClickListener {
            showDatePicker(true)
        }

        binding.cardBottomInput.setOnClickListener {
            showDatePicker(false)
        }
    }

    private fun updateDisplay() {
        binding.tvTopValue.text = startDate
        binding.tvBottomValue.text = endDate
    }

    private fun calculateResult() {
        if (startDate.isEmpty() || endDate.isEmpty()) {
            showError()
            return
        }

        val result = strategy.calculateDetailedDiff(startDate, endDate)

        if (result != null) {
            binding.tvErrorMsg.visibility = View.GONE
            binding.mcvDateResult.visibility = View.VISIBLE

            binding.tvTotalDaysNumber.text = ExpressionFormatter.formatNumberToken(result.totalDays.toString())
            binding.tvResYears.text = result.years.toString()
            binding.tvResMonths.text = result.months.toString()
            binding.tvResDays.text = result.days.toString()
            binding.tvResWeeks.text = String.format(
                Locale.US,
                getString(R.string.weeks_),
                ExpressionFormatter.formatNumberToken(result.weeks.toString())
            )
            binding.tvResHours.text = String.format(
                Locale.US,
                getString(R.string.hours),
                ExpressionFormatter.formatNumberToken(result.hours.toString())
            )
            binding.tvResMinutes.text = String.format(
                Locale.US,
                getString(R.string.minutes),
                ExpressionFormatter.formatNumberToken(result.minutes.toString())
            )
        } else {
            showError()
        }
    }

    private fun showError() {
        binding.mcvDateResult.visibility = View.GONE
        binding.tvErrorMsg.visibility = View.VISIBLE
    }

    private fun showDatePicker(isStart: Boolean) {
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

        title.text = if (isStart) getString(R.string.start_date) else getString(R.string.end_date)

        val currentStr = if (isStart) startDate else endDate
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

        val monthsArray = DateFormatSymbols.getInstance().shortMonths
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
            if (isStart) {
                startDate = dateStr
            } else {
                endDate = dateStr
            }
            updateDisplay()
            calculateResult()
            dialog.dismiss()
        }
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
