package com.shivam.simplecalculator.domain.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPrefHelper {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_THEME = "prefs_theme"
    private const val KEY_VIBRATION_ENABLED = "is_vibration_enabled"
    private const val KEY_LAST_CURRENCY_UPDATE = "last_currency_update"
    private const val KEY_LANGUAGE_CODE = "language_code"
    private const val KEY_IS_LANGUAGE_SET = "is_language_set"

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

    var lastCurrencyUpdateDate: String
        get() = preferences.getString(KEY_LAST_CURRENCY_UPDATE, "") ?: ""
        set(value) = preferences.edit { putString(KEY_LAST_CURRENCY_UPDATE, value) }

    var languageCode: String
        get() = preferences.getString(KEY_LANGUAGE_CODE, "en") ?: "en"
        set(value) = preferences.edit { putString(KEY_LANGUAGE_CODE, value) }

    var isLanguageSet: Boolean
        get() = preferences.getBoolean(KEY_IS_LANGUAGE_SET, false)
        set(value) = preferences.edit { putBoolean(KEY_IS_LANGUAGE_SET, value) }
}
