package com.shivam.simplecalculator.ui.currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shivam.simplecalculator.databinding.LayoutCurrencyBottomSheetBinding
import com.shivam.simplecalculator.models.CurrencyModel

class CurrencyBottomSheetFragment(
    private val currencies: List<CurrencyModel>,
    private val selectedCurrency: CurrencyModel?,
    private val onCurrencySelected: (CurrencyModel) -> Unit
) : BottomSheetDialogFragment() {

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

        binding.rvCurrencies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCurrencies.adapter = CurrencyAdapter(currencies, selectedCurrency) {
            onCurrencySelected(it)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
