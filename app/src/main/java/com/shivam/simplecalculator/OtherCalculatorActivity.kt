package com.shivam.simplecalculator

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class OtherCalculatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_calculator)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<LinearLayout>(R.id.btnUnitConverter).setOnClickListener {
            startActivity(Intent(this, UnitConverterActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnFinancial).setOnClickListener {
            startActivity(Intent(this, GstCalculatorActivity::class.java))
        }
    }
}
