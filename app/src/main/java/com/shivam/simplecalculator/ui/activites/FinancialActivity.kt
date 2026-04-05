package com.shivam.simplecalculator.ui.activites

import android.content.Intent
import android.os.Bundle
import com.shivam.simplecalculator.databinding.ActivityFinancialBinding

class FinancialActivity : BaseActivity() {

    private lateinit var binding : ActivityFinancialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnCurrencyScreen.setOnClickListener {
            startActivity(Intent(this, CurrencyActivity::class.java))
        }

        binding.btnGstScreen.setOnClickListener {
            startActivity(Intent(this, GstCalculatorActivity::class.java))
        }
    }
}
