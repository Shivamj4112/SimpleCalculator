package com.shivam.simplecalculator.ui.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ItemLanguageBinding
import com.shivam.simplecalculator.ui.models.LanguageModel

class LanguageAdapter(
    private var languages: List<LanguageModel>,
    private val onLanguageSelected: (LanguageModel) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedPosition = -1

    init {
        selectedPosition = languages.indexOfFirst { it.isSelected }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.bind(language, position == selectedPosition)
    }

    override fun getItemCount(): Int = languages.size

    fun getSelectedLanguage(): LanguageModel? {
        return if (selectedPosition != -1) languages[selectedPosition] else null
    }

    inner class LanguageViewHolder(private val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(language: LanguageModel, isSelected: Boolean) {
            binding.tvLanguageName.text = language.name
            binding.tvNativeName.text = "(${language.subName})"
            binding.ivFlag.setImageResource(language.flag)

            val context = binding.root.context

            val selectedColor = ContextCompat.getColor(context, R.color.ic_language_selected)
            val selectedStrokeColor = ContextCompat.getColor(context, R.color.primaryColor)
            val defaultColor = ContextCompat.getColor(context, R.color.keypad_number_bg)

            binding.ivCheckbox.isSelected = isSelected

            if (isSelected) {
                binding.cardView.strokeColor = selectedStrokeColor
                binding.cardView.backgroundTintList = ColorStateList.valueOf(selectedColor)
            } else {
                binding.cardView.strokeColor = defaultColor
                binding.cardView.backgroundTintList = ColorStateList.valueOf(defaultColor)
            }

            binding.cardView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onLanguageSelected(language)
            }
        }
    }
}
