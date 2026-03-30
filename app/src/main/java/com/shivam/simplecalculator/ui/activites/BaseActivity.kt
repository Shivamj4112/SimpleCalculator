package com.shivam.simplecalculator.ui.activites

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowInsetsControllerCompat
import com.shivam.simplecalculator.R

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val theme = sharedPreferences.getInt("prefs_theme", 0)
        when (theme) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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

    protected fun getButtonText(view: View): String {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                if (child is TextView) {
                    return child.text.toString()
                }
            }
        } else if (view is TextView) {
            return view.text.toString()
        }
        return ""
    }
}