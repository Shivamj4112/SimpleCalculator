package com.shivam.simplecalculator.ui.activites

import android.os.Bundle
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityVibrationBinding
import com.shivam.simplecalculator.domain.util.VibrationUtil

class VibrationActivity : BaseActivity() {

    private lateinit var binding: ActivityVibrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVibrationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            updateToggleUI()

            itemVibrationToggle.setOnClickListener {
                val currentState = VibrationUtil.isVibrationEnabled(this@VibrationActivity)
                VibrationUtil.setVibrationEnabled(this@VibrationActivity, !currentState)
                updateToggleUI()
            }
        }
    }

    private fun ActivityVibrationBinding.updateToggleUI() {

        val isEnabled = VibrationUtil.isVibrationEnabled(this@VibrationActivity)
        if (isEnabled) {
            ivVibrationToggle.setImageResource(R.drawable.ic_toggle_on)
        } else {
            ivVibrationToggle.setImageResource(R.drawable.ic_toggle_off)
        }
    }
}
