package com.shivam.simplecalculator.domain.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.EditText
import android.widget.NumberPicker
import com.shivam.simplecalculator.R
import java.util.Locale

@SuppressLint("SoonBlockedPrivateApi")
class CustomNumberPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : NumberPicker(context, attrs) {

    init {
        removeDivider()
        reduceItemHeight()
        styleText()
        setFormatter { value -> String.format(Locale.US, "%d", value) }
    }

    private fun styleText() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is EditText) {
                child.textSize = 16f
                child.setTextColor(context.getColor(R.color.text_color))
                child.isEnabled = false
            }
        }
    }

    private fun reduceItemHeight() {
        try {
            val field = NumberPicker::class.java.getDeclaredField("mSelectorElementHeight")
            field.isAccessible = true

            val smallerHeight = dpToPx(36)
            field.setInt(this, smallerHeight)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeDivider() {
        try {
            val field = NumberPicker::class.java.getDeclaredField("mSelectionDivider")
            field.isAccessible = true
            field.set(this, ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}