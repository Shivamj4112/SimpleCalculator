package com.shivam.simplecalculator.ui.activites

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.domain.util.SharedPrefHelper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        if (!SharedPrefHelper.isLanguageSet) {
            val intent = Intent(this, LanguageActivity::class.java)
            intent.putExtra("EXTRA_FIRST_LAUNCH", true)
            startActivity(intent)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}