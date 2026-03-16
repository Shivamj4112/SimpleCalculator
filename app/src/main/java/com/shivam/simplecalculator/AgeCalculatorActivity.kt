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
import androidx.core.graphics.drawable.toDrawable
import android.content.Intent
import android.provider.CalendarContract
import android.view.View
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AgeCalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityAgeCalculatorBinding
    private var dobCalendar = Calendar.getInstance()
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
        
        binding.btnAddCalendar.setOnClickListener { addToCalendar() }
        binding.btnShare.setOnClickListener { shareAgeDetails() }
    }

    private fun updateDisplay() {
        binding.tvDobValue.text = dateFormat.format(dobCalendar.time)
        binding.tvTodayValue.text = dateFormat.format(todayCalendar.time)
        calculateAge()
    }

    private fun calculateAge() {
        val sameDay = dobCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                dobCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)

        if (sameDay) {
            binding.mcvBirthDayResult.visibility = android.view.View.GONE
            return
        } else {
            binding.mcvBirthDayResult.visibility = android.view.View.VISIBLE
        }

        if (dobCalendar.after(todayCalendar)) {
            binding.tvYearsNumber.text = "0"
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

        binding.tvYearsNumber.text = years.toString()
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

    private fun addToCalendar() {
        val nextBirthday = dobCalendar.clone() as Calendar
        nextBirthday.set(Calendar.YEAR, todayCalendar.get(Calendar.YEAR))
        if (nextBirthday.before(todayCalendar) || nextBirthday == todayCalendar) {
            nextBirthday.add(Calendar.YEAR, 1)
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "Birthday")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, nextBirthday.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, nextBirthday.timeInMillis + 60 * 60 * 1000) // 1 hour duration
            putExtra(CalendarContract.Events.ALL_DAY, true)
        }
        startActivity(intent)
    }

    private fun shareAgeDetails() {
        val ageStr = "${binding.tvYearsNumber.text} Years, ${binding.tvMonthsDays.text.toString().replace("\n", " ")}"
        val nextBirthdayStr = "Next Birthday in: ${binding.tvNextBirthdayCountdown.text.toString().replace("\n", " ")}"
        val summaryStr = "Summary:\n" +
                "Total Years: ${binding.tvSumYears.text}\n" +
                "Total Months: ${binding.tvSumMonths.text}\n" +
                "Total Weeks: ${binding.tvSumWeeks.text}\n" +
                "Total Days: ${binding.tvSumDaysFull.text.toString().replace("Days\n", "")}\n" +
                "Total Hours: ${binding.tvSumHours.text.toString().replace("Hours\n", "")}\n" +
                "Total Minutes: ${binding.tvSumMinutes.text.toString().replace("Minutes\n", "")}"

        val shareContent = "My Age Details:\n\n$ageStr\n$nextBirthdayStr\n\n$summaryStr"

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareContent)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun showDatePicker(isDob: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_date_picker, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
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
