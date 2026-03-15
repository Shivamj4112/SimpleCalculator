package com.shivam.simplecalculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = getColor(R.color.bg_color)
        window.navigationBarColor = getColor(R.color.bg_color)
        super.onCreate(savedInstanceState)
    }

}