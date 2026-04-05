package com.shivam.simplecalculator.ui.viewmodel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shivam.simplecalculator.databinding.LayoutCurrencyBottomSheetBinding
import com.shivam.simplecalculator.domain.models.CurrencyModel
import com.shivam.simplecalculator.ui.adapter.CurrencyAdapter
import java.util.Locale

class CurrencyBottomSheetFragment : BottomSheetDialogFragment() {
    private var currencies: List<CurrencyModel> = emptyList()
    private var selectedCurrency: CurrencyModel? = null
    private var onCurrencySelected: ((CurrencyModel) -> Unit)? = null
    private lateinit var adapter: CurrencyAdapter

    companion object {
        fun newInstance(
            currencies: List<CurrencyModel>,
            selectedCurrency: CurrencyModel?,
            onCurrencySelected: (CurrencyModel) -> Unit
        ): CurrencyBottomSheetFragment {
            val fragment = CurrencyBottomSheetFragment()
            fragment.currencies = currencies
            fragment.selectedCurrency = selectedCurrency
            fragment.onCurrencySelected = onCurrencySelected
            return fragment
        }
    }

    private var _binding: LayoutCurrencyBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutCurrencyBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CurrencyAdapter(currencies, selectedCurrency) {
            onCurrencySelected?.invoke(it)
            dismiss()
        }
        binding.rvCurrencies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCurrencies.adapter = adapter

        setupSearch()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            currencies
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            currencies.filter {
                it.currencyName?.lowercase(Locale.getDefault())?.contains(lowerCaseQuery) == true ||
                it.currencyCode?.lowercase(Locale.getDefault())?.contains(lowerCaseQuery) == true ||
                it.countryName?.lowercase(Locale.getDefault())?.contains(lowerCaseQuery) == true
            }
        }
        adapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
