package com.shivam.simplecalculator.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ItemCurrencySelectorBinding
import com.shivam.simplecalculator.domain.models.CurrencyModel

class CurrencyAdapter(
    private var currencies: List<CurrencyModel>,
    private val selectedCurrency: CurrencyModel?,
    private val onItemClick: (CurrencyModel) -> Unit
) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    private var filteredList: List<CurrencyModel> = currencies

    fun updateList(newList: List<CurrencyModel>) {
        filteredList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCurrencySelectorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currency: CurrencyModel) {
            binding.tvCurrencyCode.text = currency.currencyCode
            binding.tvCurrencyName.text = currency.currencyName
            binding.tvCurrencySign.text = currency.currencySign?.let { "($it)" } ?: ""
            binding.tvCountryName.text = currency.countryName ?: ""

            binding.ivFlag.load(currency.roundIcon) {
                crossfade(true)
                placeholder(R.drawable.ic_globe)
                error(R.drawable.ic_globe)
                transformations(CircleCropTransformation())
            }

            binding.ivSelected.visibility = if (currency.currencyCode == selectedCurrency?.currencyCode) View.VISIBLE else View.GONE

            binding.root.setOnClickListener { onItemClick(currency) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCurrencySelectorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount() = filteredList.size
}