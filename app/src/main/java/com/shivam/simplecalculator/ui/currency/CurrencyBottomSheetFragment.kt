package com.shivam.simplecalculator.ui.currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shivam.simplecalculator.databinding.LayoutCurrencyBottomSheetBinding
import com.shivam.simplecalculator.models.CurrencyModel

class CurrencyBottomSheetFragment : BottomSheetDialogFragment() {
    private var currencies: List<CurrencyModel> = emptyList()
    private var selectedCurrency: CurrencyModel? = null
    private var onCurrencySelected: ((CurrencyModel) -> Unit)? = null

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

        binding.rvCurrencies.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCurrencies.adapter = CurrencyAdapter(currencies, selectedCurrency) {
            onCurrencySelected?.invoke(it)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
