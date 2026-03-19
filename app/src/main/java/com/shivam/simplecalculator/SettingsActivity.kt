package com.shivam.simplecalculator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.shivam.simplecalculator.databinding.ActivitySettingsBinding
import androidx.core.net.toUri

class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupRow(binding.itemLanguage.root, getString(R.string.app_language), getString(R.string.english), R.drawable.ic_language)
        setupRow(binding.itemTheme.root, getString(R.string.choose_theme), getString(R.string.choose_theme), R.drawable.ic_choose_theme)
        binding.itemTheme.root.setOnClickListener {
            startActivity(Intent(this, ChooseThemeActivity::class.java))
        }
        setupRow(binding.itemVibration.root,
            getString(R.string.vibration),
            getString(R.string.vibrate_when_a_button_is_pressed), R.drawable.ic_vibration)
        setupRow(binding.itemCallerSettings.root,
            getString(R.string.caller_settings), "", R.drawable.ic_caller)
            
        setupRow(binding.itemShareApp.root,
            getString(R.string.share_app), "", R.drawable.ic_share)
        binding.itemShareApp.root.setOnClickListener { shareApp() }

        setupRow(binding.itemRateUs.root,
            getString(R.string.rate_us), "", R.drawable.ic_rate)
        binding.itemRateUs.root.setOnClickListener { rateUs() }

        setupRow(binding.itemPrivacyPolicy.root,
            getString(R.string.privacy_policy), "", R.drawable.ic_privacy_policy)
        binding.itemPrivacyPolicy.root.setOnClickListener { openPrivacyPolicy() }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareMessage = "Check out this Simple Calculator app: https://play.google.com/store/apps/details?id=${packageName}"
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
