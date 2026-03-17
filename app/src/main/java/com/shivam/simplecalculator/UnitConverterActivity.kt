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
            val intent = android.content.Intent(this, ConverterActivity::class.java)
            intent.putExtra(ConverterActivity.EXTRA_TYPE, com.shivam.simplecalculator.models.ConverterType.BMI.name)
            startActivity(intent)
        }

        binding.btnArea.setOnClickListener {
            startActivity(android.content.Intent(this, AreaCalculatorActivity::class.java))
        }

        val launchConverter = { type: com.shivam.simplecalculator.models.ConverterType ->
            val intent = android.content.Intent(this, ConverterActivity::class.java)
            intent.putExtra(ConverterActivity.EXTRA_TYPE, type.name)
            startActivity(intent)
        }

        binding.btnData?.setOnClickListener { launchConverter(com.shivam.simplecalculator.models.ConverterType.DATA) }
        binding.btnDiscount?.setOnClickListener { launchConverter(com.shivam.simplecalculator.models.ConverterType.DISCOUNT) }
        binding.btnLength?.setOnClickListener { launchConverter(com.shivam.simplecalculator.models.ConverterType.LENGTH) }
        binding.btnMass?.setOnClickListener { launchConverter(com.shivam.simplecalculator.models.ConverterType.MASS) }
        binding.btnNumeral?.setOnClickListener { launchConverter(com.shivam.simplecalculator.models.ConverterType.NUMERAL) }
        binding.btnSpeed?.setOnClickListener { launchConverter(com.shivam.simplecalculator.models.ConverterType.SPEED) }
        binding.btnTemperature?.setOnClickListener { launchConverter(com.shivam.simplecalculator.models.ConverterType.TEMPERATURE) }
        binding.btnTime?.setOnClickListener { launchConverter(com.shivam.simplecalculator.models.ConverterType.TIME) }
        binding.btnVolume?.setOnClickListener { launchConverter(com.shivam.simplecalculator.models.ConverterType.VOLUME) }

    }
}
