package com.shivam.simplecalculator

import android.app.Application
import com.shivam.simplecalculator.domain.util.SharedPrefHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CalculatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefHelper.init(this)
    }
}
