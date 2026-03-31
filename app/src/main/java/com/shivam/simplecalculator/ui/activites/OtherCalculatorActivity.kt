package com.shivam.simplecalculator.ui.activites

import android.content.Intent
import android.os.Bundle
import com.shivam.simplecalculator.databinding.ActivityOtherCalculatorBinding


class OtherCalculatorActivity : BaseActivity() {

    private lateinit var binding: ActivityOtherCalculatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUnitConverter.setOnClickListener {
            startActivity(Intent(this, UnitConverterActivity::class.java))
        }

        binding.btnFinancial.setOnClickListener {
            startActivity(Intent(this, FinancialActivity::class.java))
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(com.shivam.simplecalculator.R.anim.slide_in_right, com.shivam.simplecalculator.R.anim.slide_out_left)
    }
}
