package com.shivam.simplecalculator

import android.os.Bundle
import android.widget.ImageView
import com.shivam.simplecalculator.databinding.ActivityUnitConverterBinding

class UnitConverterActivity : BaseActivity() {

    private lateinit var binding: ActivityUnitConverterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnitConverterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAge.setOnClickListener {
            startActivity(android.content.Intent(this, AgeCalculatorActivity::class.java))
        }

        binding.btnBmi.setOnClickListener {
            startActivity(android.content.Intent(this, BmiCalculatorActivity::class.java))
        }

        binding.btnArea.setOnClickListener {
            startActivity(android.content.Intent(this, AreaCalculatorActivity::class.java))
        }
    }
}
