package com.shivam.simplecalculator

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shivam.simplecalculator.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupRow(binding.itemLanguage.root, "App language", "English", R.drawable.ic_calc) // Replace with ic_language
        setupRow(binding.itemTheme.root, "Choose theme", "System default", R.drawable.ic_settings)
        setupRow(binding.itemScientific.root, "Scientific mode", "DeActivate scientific mode", R.drawable.ic_calc)
        setupRow(binding.itemNumbering.root, "Numbering system", "International", R.drawable.ic_settings)
        setupRow(binding.itemVibration.root, "Vibration", "vibrate when a button is pressed", R.drawable.ic_settings)
        setupRow(binding.itemRadians.root, "Radians", "Use radians instead of degrees by default", R.drawable.ic_calc)
        setupRow(binding.itemParentheses.root, "Split the parentheses button into two buttons", "Remove the \"c\" button to split the parentheses button into two buttons", R.drawable.ic_settings)
        setupRow(binding.itemModulo.root, "Add a modulo button", "Replace the \"X2\" button with a modulo button\n(available when the \"INV\" button is pressed)", R.drawable.ic_calc)
        setupRow(binding.itemLockScreen.root, "Show on lock screen", "Keep the calculator visible on your lock screen for quick and easy access, disable to hide it when your screen is locked.", R.drawable.ic_settings)

        setupRow(binding.itemDecimalPlaces.root, "Number of decimal places", "10", R.drawable.ic_settings)
        setupRow(binding.itemScientificNotation.root, "Number into scientific notation", "Convert all results to scientific notation", R.drawable.ic_calc)

        setupRow(binding.itemLongPressCopy.root, "Long press to copy value", "Long press copies the text of the item instead of selecting it", R.drawable.ic_copy)
        setupRow(binding.itemKeepAwake.root, "Keep device awake", "Prevent device from sleeping while the app is in the foreground", R.drawable.ic_settings)

        setupRow(binding.itemCallerSettings.root, "Caller Settings", "", R.drawable.ic_calc)
    }

    private fun setupRow(view: android.view.View, title: String, subtitle: String, icon: Int) {
        view.findViewById<TextView>(R.id.tvTitle).text = title
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)
        if (subtitle.isEmpty()) {
            tvSubtitle.visibility = android.view.View.GONE
        } else {
            tvSubtitle.text = subtitle
        }
        view.findViewById<ImageView>(R.id.ivIcon).setImageResource(icon)
    }
}
