package com.shivam.simplecalculator

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val theme = sharedPreferences.getInt("prefs_theme", 0)
        when (theme) {
            0 -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)
            2 -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
        }


        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.bg_color)
        window.navigationBarColor = getColor(R.color.bg_color)

        setAutoDarkStatusBarIconColor()
    }

    fun setAutoDarkStatusBarIconColor(){
        val isDarkMode =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                    Configuration.UI_MODE_NIGHT_YES

        val controller = WindowInsetsControllerCompat(window, window.decorView)

        controller.isAppearanceLightStatusBars = !isDarkMode
        controller.isAppearanceLightNavigationBars = !isDarkMode
    }

    protected fun getButtonText(view: android.view.View): String {
        if (view is android.view.ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                if (child is android.widget.TextView) {
                    return child.text.toString()
                }
            }
        } else if (view is android.widget.TextView) {
            return view.text.toString()
        }
        return ""
    }
}