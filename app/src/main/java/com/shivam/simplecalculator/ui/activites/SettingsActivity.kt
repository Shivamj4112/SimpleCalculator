package com.shivam.simplecalculator.ui.activites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivitySettingsBinding
import com.shivam.simplecalculator.domain.util.SharedPrefHelper

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupRow(
            binding.itemLanguage.root,
            getString(R.string.app_language),
            getString(R.string.english),
            R.drawable.ic_language
        )

        binding.itemTheme.root.setOnClickListener {
            startActivity(Intent(this, ChooseThemeActivity::class.java))
        }
        setupRow(
            binding.itemVibration.root,
            getString(R.string.vibration),
            getString(R.string.vibrate_when_a_button_is_pressed), R.drawable.ic_vibration
        )
        binding.itemVibration.root.setOnClickListener {
            startActivity(Intent(this, VibrationActivity::class.java))
        }
        setupRow(
            binding.itemCallerSettings.root,
            getString(R.string.caller_settings), "", R.drawable.ic_caller
        )
        binding.itemCallerSettings.root.setOnClickListener {
            startActivity(Intent(this, CallerSettingsActivity::class.java))
        }

        setupRow(
            binding.itemShareApp.root,
            getString(R.string.share_app), "", R.drawable.ic_share
        )
        binding.itemShareApp.root.setOnClickListener { shareApp() }

        setupRow(
            binding.itemRateUs.root,
            getString(R.string.rate_us), "", R.drawable.ic_rate
        )
        binding.itemRateUs.root.setOnClickListener { rateUs() }

        setupRow(
            binding.itemPrivacyPolicy.root,
            getString(R.string.privacy_policy), "", R.drawable.ic_privacy_policy
        )
        binding.itemPrivacyPolicy.root.setOnClickListener { openPrivacyPolicy() }

        updateThemeRow()
    }


    private fun updateThemeRow() {
        val theme = SharedPrefHelper.theme
        val themeSubtitle = when (theme) {
            1 -> getString(R.string.light)
            2 -> getString(R.string.dark)
            else -> getString(R.string.system_default)
        }
        setupRow(
            binding.itemTheme.root,
            getString(R.string.choose_theme),
            themeSubtitle,
            R.drawable.ic_choose_theme
        )
    }

    private fun shareApp() {
        val link = "https://play.google.com/store/apps/details?id=${packageName}"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareMessage = getString(R.string.check_out_this_simple_calculator_app, link)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun rateUs() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = "market://details?id=${packageName}".toUri()
        try {
            startActivity(intent)
        } catch (e: Exception) {
            val browserIntent = Intent(Intent.ACTION_VIEW)
            browserIntent.data =
                "https://play.google.com/store/apps/details?id=${packageName}".toUri()
            startActivity(browserIntent)
        }
    }

    private fun openPrivacyPolicy() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = "https://www.google.com".toUri()
        startActivity(intent)
    }

    private fun setupRow(view: View, title: String, subtitle: String, icon: Int) {
        view.findViewById<TextView>(R.id.tvTitle).text = title
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)
        if (subtitle.isEmpty()) {
            tvSubtitle.visibility = View.GONE
        } else {
            tvSubtitle.text = subtitle
            tvSubtitle.visibility = View.VISIBLE
        }
        view.findViewById<ImageView>(R.id.ivIcon).setImageResource(icon)
    }
}
