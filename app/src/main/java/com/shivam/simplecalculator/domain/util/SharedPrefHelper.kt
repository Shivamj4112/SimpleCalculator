package com.shivam.simplecalculator.domain.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPrefHelper {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_THEME = "prefs_theme"
    private const val KEY_VIBRATION_ENABLED = "is_vibration_enabled"

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var theme: Int
        get() = preferences.getInt(KEY_THEME, 0)
        set(value) = preferences.edit { putInt(KEY_THEME, value) }

    var vibrationEnabled: Boolean
        get() = preferences.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = preferences.edit { putBoolean(KEY_VIBRATION_ENABLED, value) }
}
