package com.shivam.simplecalculator.domain.util.strategies

import com.shivam.simplecalculator.domain.models.UnitOption
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class DateDiffResult(
    val years: Long,
    val months: Long,
    val weeks: Long,
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val totalMonths: Long,
    val totalDays: Long,
    val breakdownDays: Int,
    val breakdownWeeks: Int
)

class DateStrategy : ConverterStrategy {
    override fun convert(value1: Double, value2: Double, from: UnitOption, to: UnitOption): Double {
        return 0.0
    }

    fun calculateDays(startDateStr: String, endDateStr: String): Double {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val start = LocalDate.parse(startDateStr, formatter)
            val end = LocalDate.parse(endDateStr, formatter)
            
            val daysDiff = Math.abs(ChronoUnit.DAYS.between(start, end)).toDouble()
            daysDiff
        } catch (e: Exception) {
            0.0
        }
    }

    fun calculateDetailedDiff(startDateStr: String, endDateStr: String): DateDiffResult? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            var start = LocalDate.parse(startDateStr, formatter)
            var end = LocalDate.parse(endDateStr, formatter)

            if (end.isBefore(start)) {
                val temp = start
                start = end
                end = temp
            }

            val period = Period.between(start, end)
            val totalDays = ChronoUnit.DAYS.between(start, end)
            val totalMonths = ChronoUnit.MONTHS.between(start, end)
            
            val years = period.years.toLong()
            val months = period.months.toLong()
            val days = period.days.toLong()
            
            val weeks = days / 7
            val remainingDays = days % 7

            DateDiffResult(
                years = years,
                months = months,
                weeks = weeks,
                days = remainingDays,
                hours = totalDays * 24,
                minutes = totalDays * 24 * 60,
                totalMonths = totalMonths,
                totalDays = totalDays,
                breakdownDays = remainingDays.toInt(),
                breakdownWeeks = weeks.toInt()
            )
        } catch (e: Exception) {
            null
        }
    }
}
