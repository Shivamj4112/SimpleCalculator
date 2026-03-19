package com.shivam.simplecalculator

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.shivam.simplecalculator.databinding.ActivityChooseThemeBinding

class ChooseThemeActivity : BaseActivity() {

    private lateinit var binding: ActivityChooseThemeBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        binding.btnBack.setOnClickListener { finish() }

        val currentTheme = sharedPreferences.getInt("prefs_theme", 0) // 0: Default, 1: Light, 2: Dark
        updateUI(currentTheme)

        binding.llLightTheme.setOnClickListener {
            saveTheme(1)
        }

        binding.llDarkTheme.setOnClickListener {
            saveTheme(2)
        }

        binding.llSystemDefault.setOnClickListener {
            val isChecked = sharedPreferences.getInt("prefs_theme", 0) == 0
            if (isChecked) {
                // If already system default, maybe switch to light? 
                // But the UI shows it as a toggle. Let's toggle between System Default and previous choice or just Light.
                // According to requirements "it should toggle between this 3 themes".
                // Usually "System default" is a separate toggle or the 3rd option.
                // In the image, "System default" is at the bottom with a switch.
                saveTheme(1) // Default to light if toggled off? Or just keep it as 3 options.
            } else {
                saveTheme(0)
            }
        }
        
        // If they click the row, toggle it
        binding.llSystemDefault.setOnClickListener {
            val current = sharedPreferences.getInt("prefs_theme", 0)
            if (current == 0) {
                saveTheme(1) // Switch to Light if System Default is turned off
            } else {
                saveTheme(0) // Switch to System Default
            }
        }
    }

    private fun saveTheme(theme: Int) {
        sharedPreferences.edit().putInt("prefs_theme", theme).apply()
        updateUI(theme)
        
        when (theme) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun updateUI(theme: Int) {
        binding.ivLightSelected.setImageResource(if (theme == 1) R.drawable.ic_checked else R.drawable.ic_unchecked)
        binding.ivDarkSelected.setImageResource(if (theme == 2) R.drawable.ic_checked else R.drawable.ic_unchecked)
        binding.ivSystemDefaultToggle.setImageResource(if (theme == 0) R.drawable.ic_checked else R.drawable.ic_unchecked)
        
        // If system default is on, the light/dark cards should probably look disabled or just unselected.
        // The image shows "Light" selected while "System default" is off.
    }
}
