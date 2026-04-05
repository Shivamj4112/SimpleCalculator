package com.shivam.simplecalculator.ui.activites

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.shivam.simplecalculator.databinding.ActivityLanguageBinding
import com.shivam.simplecalculator.domain.util.SharedPrefHelper
import com.shivam.simplecalculator.domain.util.Util.getLanguageList
import com.shivam.simplecalculator.ui.adapters.LanguageAdapter
import java.util.Locale

class LanguageActivity : BaseActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var adapter: LanguageAdapter
    private var isFirstLaunch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isFirstLaunch = intent.getBooleanExtra("EXTRA_FIRST_LAUNCH", false)

        if (isFirstLaunch) {
            binding.btnBack.visibility = View.GONE
        }

        setupRecyclerView()

        binding.btnBack.setOnClickListener {
            if (isFirstLaunch) {
                saveSelectedLanguage()
            } else {
                finish()
            }
        }

        binding.btnSave.setOnClickListener {
            saveSelectedLanguage()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFirstLaunch) {
                    saveSelectedLanguage()
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupRecyclerView() {
        val languages = getLanguageList()
        val currentLang = if (isFirstLaunch) {
            Locale.getDefault().language
        } else {
            SharedPrefHelper.languageCode
        }

        languages.forEach {
            if (it.code == currentLang) it.isSelected = true 
        }
        
        if (languages.none { it.isSelected }) {
            languages.firstOrNull()?.isSelected = true
        }

        adapter = LanguageAdapter(languages) { _ ->
        }

        binding.rvLanguages.layoutManager = LinearLayoutManager(this)
        binding.rvLanguages.adapter = adapter
    }

    private fun saveSelectedLanguage() {
        adapter.getSelectedLanguage()?.let { selected ->
            SharedPrefHelper.languageCode = selected.code
            SharedPrefHelper.isLanguageSet = true
            
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }




}
