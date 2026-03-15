package com.shivam.simplecalculator

import android.os.Bundle
import android.widget.ImageView


class UnitConverterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_converter)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btnAge).setOnClickListener {
            startActivity(android.content.Intent(this, AgeCalculatorActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnBmi).setOnClickListener {
            startActivity(android.content.Intent(this, BmiCalculatorActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnArea).setOnClickListener {
            startActivity(android.content.Intent(this, AreaCalculatorActivity::class.java))
        }
    }
}
