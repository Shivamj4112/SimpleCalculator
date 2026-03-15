package com.shivam.simplecalculator

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView

import androidx.cardview.widget.CardView

class FinancialActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<CardView>(R.id.btnGstScreen).setOnClickListener {
            startActivity(Intent(this, GstCalculatorActivity::class.java))
        }
    }
}
