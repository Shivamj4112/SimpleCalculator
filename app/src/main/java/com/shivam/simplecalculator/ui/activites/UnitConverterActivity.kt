package com.shivam.simplecalculator.ui.activites

import android.content.Intent
import android.os.Bundle
import com.shivam.simplecalculator.databinding.ActivityUnitConverterBinding
import com.shivam.simplecalculator.domain.models.ConverterType

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
            startActivity(Intent(this, AgeCalculatorActivity::class.java))
        }

        binding.btnBmi.setOnClickListener {
            val intent = Intent(this, ConverterActivity::class.java)
            intent.putExtra(
                ConverterActivity.EXTRA_TYPE,
                ConverterType.BMI.name
            )
            startActivity(intent)
        }

        binding.btnArea.setOnClickListener {
            startActivity(Intent(this, AreaCalculatorActivity::class.java))
        }

        val launchConverter = { type: ConverterType ->
            val intent = Intent(this, ConverterActivity::class.java)
            intent.putExtra(ConverterActivity.EXTRA_TYPE, type.name)
            startActivity(intent)
        }

        binding.btnData?.setOnClickListener { launchConverter(ConverterType.DATA) }
        binding.btnDate?.setOnClickListener { launchConverter(ConverterType.DATE) }
        binding.btnDiscount?.setOnClickListener { launchConverter(ConverterType.DISCOUNT) }
        binding.btnLength?.setOnClickListener { launchConverter(ConverterType.LENGTH) }
        binding.btnMass?.setOnClickListener { launchConverter(ConverterType.MASS) }
        binding.btnNumeral?.setOnClickListener { launchConverter(ConverterType.NUMERAL) }
        binding.btnSpeed?.setOnClickListener { launchConverter(ConverterType.SPEED) }
        binding.btnTemperature?.setOnClickListener { launchConverter(ConverterType.TEMPERATURE) }
        binding.btnTime?.setOnClickListener { launchConverter(ConverterType.TIME) }
        binding.btnVolume?.setOnClickListener { launchConverter(ConverterType.VOLUME) }

    }
}
