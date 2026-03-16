package com.shivam.simplecalculator

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView

import androidx.cardview.widget.CardView
import com.shivam.simplecalculator.databinding.ActivityFinancialBinding

class FinancialActivity : BaseActivity() {

    private lateinit var binding : ActivityFinancialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinancialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnGstScreen.setOnClickListener {
            startActivity(Intent(this, GstCalculatorActivity::class.java))
        }
    }
}
