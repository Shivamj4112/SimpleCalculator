package com.shivam.simplecalculator.ui.activites

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityCallerSettingsBinding
import com.shivam.simplecalculator.databinding.ActivityVibrationBinding
import com.shivam.simplecalculator.domain.util.VibrationUtil

class CallerSettingsActivity : BaseActivity() {

    private lateinit var binding: ActivityCallerSettingsBinding
    private var isEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallerSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            updateToggleUI()

            itemVibrationToggle.setOnClickListener {
                updateToggleUI()
            }
        }
    }

    private fun ActivityCallerSettingsBinding.updateToggleUI() {

        if (isEnabled) {
            ivVibrationToggle.setImageResource(R.drawable.ic_toggle_on)
        } else {
            ivVibrationToggle.setImageResource(R.drawable.ic_toggle_off)
        }

        isEnabled = !isEnabled
    }
}