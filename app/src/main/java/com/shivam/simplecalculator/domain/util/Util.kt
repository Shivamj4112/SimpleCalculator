package com.shivam.simplecalculator.domain.util

import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.ui.models.LanguageModel

object Util {

    fun getLanguageList(): List<LanguageModel> {
        return listOf(
            LanguageModel("English", "English", "en", R.drawable.ic_english),
            LanguageModel("Spanish", "Española", "es", R.drawable.ic_spanish),
            LanguageModel("French", "Français", "fr", R.drawable.ic_french),
            LanguageModel("Portuguese", "Português", "pt", R.drawable.ic_portugese),
            LanguageModel("Arabic", "عربي", "ar", R.drawable.ic_africa),
            LanguageModel("Russian", "Русский", "ru", R.drawable.ic_russian),
            LanguageModel("Korean", "한국인", "ko", R.drawable.ic_korean),
            LanguageModel("German", "Deutsch", "de", R.drawable.ic_german),
            LanguageModel("Turkish", "Türkçe", "tr", R.drawable.ic_turkish),
            LanguageModel("Italian", "Italiana", "it", R.drawable.ic_italian),
            LanguageModel("Vietnamese", "Việt Nam", "vi", R.drawable.ic_vietname),
            LanguageModel("Japanese", "日本語", "ja", R.drawable.ic_japanese),
            LanguageModel("Indonesian", "Indonesia", "id", R.drawable.ic_indonesian),
            LanguageModel("Thai", "ไทย", "th", R.drawable.ic_thai),
            LanguageModel("Polish", "Polski", "pl", R.drawable.ic_polish),
            LanguageModel("Chinese", "中国人", "zh", R.drawable.ic_chinese),
            LanguageModel("Romanian", "Română", "ro", R.drawable.ic_romanian),
            LanguageModel("Hindi", "हिंदी", "hi", R.drawable.ic_hindi),
            LanguageModel("Afrikaans", "Afrikaans", "af", R.drawable.ic_africa),
            LanguageModel("Hungarian", "Magyar", "hu", R.drawable.ic_hungarian),
            LanguageModel("Ukrainian", "українська", "uk", R.drawable.ic_ukrainian),
            LanguageModel("Filipino", "Filipino", "fil", R.drawable.ic_filipino)
        )
    }

}