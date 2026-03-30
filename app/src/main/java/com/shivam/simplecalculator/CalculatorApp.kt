package com.shivam.simplecalculator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CalculatorApp : Application() {
    override fun onCreate() {
        super.onCreate()

    }
}
