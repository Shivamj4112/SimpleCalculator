package com.shivam.simplecalculator

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

import com.shivam.simplecalculator.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupRow(binding.itemLanguage.root, getString(R.string.app_language), getString(R.string.english), R.drawable.ic_language)
        setupRow(binding.itemTheme.root, getString(R.string.choose_theme), getString(R.string.choose_theme), R.drawable.ic_choose_theme)
        setupRow(binding.itemVibration.root,
            getString(R.string.vibration),
            getString(R.string.vibrate_when_a_button_is_pressed), R.drawable.ic_vibration)
        setupRow(binding.itemCallerSettings.root,
            getString(R.string.caller_settings), "", R.drawable.ic_caller)
        setupRow(binding.itemShareApp.root,
            getString(R.string.share_app), "", R.drawable.ic_share)
        setupRow(binding.itemRateUs.root,
            getString(R.string.rate_us), "", R.drawable.ic_rate)
        setupRow(binding.itemPrivacyPolicy.root,
            getString(R.string.privacy_policy), "", R.drawable.ic_privacy_policy)
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
