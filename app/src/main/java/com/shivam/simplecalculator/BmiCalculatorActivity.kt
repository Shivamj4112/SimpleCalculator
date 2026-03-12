package com.shivam.simplecalculator

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shivam.simplecalculator.databinding.ActivityBmiCalculatorBinding
import java.util.Locale

class BmiCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBmiCalculatorBinding
    private var isWeightFocused = true
    private var weightValue = ""
    private var heightValue = ""
    private var weightUnit = "Kilograms"
    private var heightUnit = "Centimeters"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        updateFocus()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cardWeight.setOnClickListener {
            isWeightFocused = true
            updateFocus()
        }

        binding.cardHeight.setOnClickListener {
            isWeightFocused = false
            updateFocus()
        }

        binding.ivWeightDropdown.setOnClickListener { showUnitDialog(true) }
        binding.ivHeightDropdown.setOnClickListener { showUnitDialog(false) }

        // Numpad
        val numpad = binding.numpad
        val buttons = listOf(
            numpad.btn0, numpad.btn1, numpad.btn2, numpad.btn3,
            numpad.btn4, numpad.btn5, numpad.btn6, numpad.btn7,
            numpad.btn8, numpad.btn9, numpad.btnDot
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                appendChar((it as Button).text.toString())
            }
        }

        numpad.btnAC.setOnClickListener {
            weightValue = ""
            heightValue = ""
            updateDisplay()
        }

        numpad.btnDel.setOnClickListener {
            if (isWeightFocused) {
                if (weightValue.isNotEmpty()) weightValue = weightValue.dropLast(1)
            } else {
                if (heightValue.isNotEmpty()) heightValue = heightValue.dropLast(1)
            }
            updateDisplay()
        }
    }

    private fun appendChar(char: String) {
        if (isWeightFocused) {
            if (char == "." && weightValue.contains(".")) return
            weightValue += char
        } else {
            if (char == "." && heightValue.contains(".")) return
            heightValue += char
        }
        updateDisplay()
    }

    private fun updateDisplay() {
        binding.tvWeightValue.text = if (weightValue.isEmpty()) "0" else weightValue
        binding.tvHeightValue.text = if (heightValue.isEmpty()) "0" else heightValue
        binding.tvWeightUnit.text = weightUnit
        binding.tvHeightUnit.text = heightUnit
        
        // Auto-calculate on entry
        calculateBmi()
    }

    private fun updateFocus() {
        binding.cardWeight.setCardBackgroundColor(if (isWeightFocused) Color.parseColor("#E0E0E0") else Color.WHITE)
        binding.cardHeight.setCardBackgroundColor(if (!isWeightFocused) Color.parseColor("#E0E0E0") else Color.WHITE)
    }

    private fun calculateBmi() {
        val w = weightValue.toDoubleOrNull() ?: 0.0
        val h = heightValue.toDoubleOrNull() ?: 0.0
        
        if (w > 0 && h > 0) {
            val weightInKg = if (weightUnit == "Pounds") w * 0.453592 else w
            val heightInM = when (heightUnit) {
                "Centimeters" -> h / 100.0
                "Feet+Inches" -> (h * 0.0254) // Placeholder for combined logic
                "Inches" -> h * 0.0254
                else -> h / 100.0
            }
            
            val bmi = weightInKg / (heightInM * heightInM)
            binding.tvBmiResultValue.text = String.format(Locale.US, "%.1f", bmi)
            binding.tvBmiResultLabel.visibility = View.VISIBLE
            binding.tvBmiResultValue.visibility = View.VISIBLE
        } else {
            binding.tvBmiResultLabel.visibility = View.GONE
            binding.tvBmiResultValue.visibility = View.GONE
        }
    }

    private fun showUnitDialog(isWeight: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_unit_selection, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val title = dialogView.findViewById<TextView>(R.id.tvSelectionTitle)
        val tvOpt1 = dialogView.findViewById<TextView>(R.id.tvOption1)
        val tvOpt2 = dialogView.findViewById<TextView>(R.id.tvOption2)
        val cb1 = dialogView.findViewById<CheckBox>(R.id.cbOption1)
        val cb2 = dialogView.findViewById<CheckBox>(R.id.cbOption2)

        if (isWeight) {
            title.text = "Weight"
            tvOpt1.text = "Kilograms"
            tvOpt2.text = "Pounds"
            cb1.isChecked = weightUnit == "Kilograms"
            cb2.isChecked = weightUnit == "Pounds"
        } else {
            title.text = "Height"
            tvOpt1.text = "Centimeters"
            tvOpt2.text = "Inches"
            cb1.isChecked = heightUnit == "Centimeters"
            cb2.isChecked = heightUnit == "Inches"
        }

        dialogView.findViewById<View>(R.id.option1).setOnClickListener {
            if (isWeight) weightUnit = "Kilograms" else heightUnit = "Centimeters"
            updateDisplay()
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.option2).setOnClickListener {
            if (isWeight) weightUnit = "Pounds" else heightUnit = "Inches"
            updateDisplay()
            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.btnCloseSelection).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
