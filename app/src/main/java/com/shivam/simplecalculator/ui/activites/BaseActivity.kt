package com.shivam.simplecalculator.ui.activites

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowInsetsControllerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.domain.util.LocaleHelper
import com.shivam.simplecalculator.domain.util.SharedPrefHelper

abstract class BaseActivity : AppCompatActivity() {
    
    companion object {
        const val ACTION_LANGUAGE_CHANGED = "com.shivam.simplecalculator.ACTION_LANGUAGE_CHANGED"
    }

    private val languageChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            recreate()
        }
    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = SharedPrefHelper.theme
        when (theme) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }


        super.onCreate(savedInstanceState)
        
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(languageChangeReceiver, IntentFilter(ACTION_LANGUAGE_CHANGED))
            

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

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(languageChangeReceiver)
        super.onDestroy()
    }
}