package com.shivam.simplecalculator

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import com.shivam.simplecalculator.databinding.ActivityAgeCalculatorBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AgeCalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityAgeCalculatorBinding
    private var dobCalendar = Calendar.getInstance().apply { set(2000, 0, 1) }
    private var todayCalendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgeCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        updateDisplay()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.cardDob.setOnClickListener { showDatePicker(true) }
        binding.cardToday.setOnClickListener { showDatePicker(false) }
        
        binding.btnAddCalendar.setOnClickListener { /* TODO */ }
        binding.btnShare.setOnClickListener { /* TODO */ }
    }

    private fun updateDisplay() {
        binding.tvDobValue.text = dateFormat.format(dobCalendar.time)
        binding.tvTodayValue.text = dateFormat.format(todayCalendar.time)
        calculateAge()
    }

    private fun calculateAge() {
        if (dobCalendar.after(todayCalendar)) {
            binding.tvYears.text = "0"
            binding.tvMonthsDays.text = "Error\nInvalid Date"
            return
        }

        var years = todayCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
        var months = todayCalendar.get(Calendar.MONTH) - dobCalendar.get(Calendar.MONTH)
        var days = todayCalendar.get(Calendar.DAY_OF_MONTH) - dobCalendar.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months--
            val lastMonth = (todayCalendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            days += lastMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        if (months < 0) {
            years--
            months += 12
        }

        binding.tvYears.text = years.toString()
        binding.tvMonthsDays.text = "$months Month\n$days Day"

        // Next Birthday
        val nextBirthday = dobCalendar.clone() as Calendar
        nextBirthday.set(Calendar.YEAR, todayCalendar.get(Calendar.YEAR))
        if (nextBirthday.before(todayCalendar) || nextBirthday == todayCalendar) {
            nextBirthday.add(Calendar.YEAR, 1)
        }

        val dayOfWeek = SimpleDateFormat("EEEE", Locale.US).format(nextBirthday.time)
        binding.tvNextBirthdayDay.text = dayOfWeek

        var nbMonths = nextBirthday.get(Calendar.MONTH) - todayCalendar.get(Calendar.MONTH)
        var nbDays = nextBirthday.get(Calendar.DAY_OF_MONTH) - todayCalendar.get(Calendar.DAY_OF_MONTH)

        if (nbDays < 0) {
            nbMonths--
            val lastMonth = (nextBirthday.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            nbDays += lastMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (nbMonths < 0) nbMonths += 12

        binding.tvNextBirthdayCountdown.text = "$nbMonths Months\n$nbDays Day"

        // Summary
        val diffMillis = todayCalendar.timeInMillis - dobCalendar.timeInMillis
        val totalDays = TimeUnit.MILLISECONDS.toDays(diffMillis)
        val totalWeeks = totalDays / 7
        val totalHours = TimeUnit.MILLISECONDS.toHours(diffMillis)
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
        
        // Approximate months
        val totalMonths = (years * 12) + months

        binding.tvSumYears.text = years.toString()
        binding.tvSumDaysFull.text = "Days\n$totalDays"
        
        binding.tvSumMonths.text = totalMonths.toString()
        binding.tvSumHours.text = "Hours\n$totalHours"
        
        binding.tvSumWeeks.text = totalWeeks.toString()
        binding.tvSumMinutes.text = "Minutes\n$totalMinutes"
    }

    private fun showDatePicker(isDob: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_picker, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val title = dialogView.findViewById<TextView>(R.id.tvPickerTitle)
        val npDay = dialogView.findViewById<NumberPicker>(R.id.npDay)
        val npMonth = dialogView.findViewById<NumberPicker>(R.id.npMonth)
        val npYear = dialogView.findViewById<NumberPicker>(R.id.npYear)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)
        val btnClose = dialogView.findViewById<ImageView>(R.id.btnClosePicker)

        title.text = if (isDob) "Date of birth" else "Today"
        val currentCal = if (isDob) dobCalendar else todayCalendar

        npYear.minValue = 1900
        npYear.maxValue = 2100
        npYear.value = currentCal.get(Calendar.YEAR)

        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        npMonth.minValue = 0
        npMonth.maxValue = 11
        npMonth.displayedValues = months
        npMonth.value = currentCal.get(Calendar.MONTH)

        npDay.minValue = 1
        npDay.maxValue = currentCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        npDay.value = currentCal.get(Calendar.DAY_OF_MONTH)

        val updateDays = {
            val tempCal = Calendar.getInstance()
            tempCal.set(npYear.value, npMonth.value, 1)
            npDay.maxValue = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        npMonth.setOnValueChangedListener { _, _, _ -> updateDays() }
        npYear.setOnValueChangedListener { _, _, _ -> updateDays() }

        btnOk.setOnClickListener {
            if (isDob) {
                dobCalendar.set(npYear.value, npMonth.value, npDay.value, 0, 0, 0)
            } else {
                todayCalendar.set(npYear.value, npMonth.value, npDay.value, 0, 0, 0)
            }
            updateDisplay()
            dialog.dismiss()
        }
        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
