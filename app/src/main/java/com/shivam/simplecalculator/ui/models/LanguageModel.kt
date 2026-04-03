package com.shivam.simplecalculator.ui.models

data class LanguageModel(
    val name: String,
    val subName: String,
    val code: String,
    val flag: Int,
    var isSelected: Boolean = false
)
