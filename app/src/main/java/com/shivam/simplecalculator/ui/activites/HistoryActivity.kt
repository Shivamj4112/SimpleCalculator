package com.shivam.simplecalculator.ui.activites

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityHistoryBinding
import com.shivam.simplecalculator.ui.viewmodel.CalculatorViewModel
import com.shivam.simplecalculator.ui.adapter.HistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: CalculatorViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(
            onItemClick = { history ->
                finish()
            },
            onItemLongClick = {
                // Handled in adapter, selection mode toggled
            },
            onSelectionChange = { count ->
                updateHistorySelectionUI(count)
            }
        )
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }
    }

    private fun updateHistorySelectionUI(count: Int) {
        if (historyAdapter.isSelectionMode) {
            binding.tvHistoryTitle.text = if (count > 0) getString(
                R.string.selected,
                count
            ) else getString(R.string.select_items)
            binding.cbSelectAll.visibility = View.VISIBLE
            binding.btnClearHistory.visibility = View.GONE
            binding.selectionActionBar.visibility = View.VISIBLE
            
            // Update Checkbox state
            binding.cbSelectAll.isChecked = count > 0 && count == historyAdapter.currentList.size
        } else {
            binding.tvHistoryTitle.text = getString(R.string.history)
            binding.cbSelectAll.visibility = View.GONE
            binding.cbSelectAll.isChecked = false
            binding.btnClearHistory.visibility = View.VISIBLE
            binding.selectionActionBar.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            if (historyAdapter.isSelectionMode) {
                historyAdapter.toggleSelectionMode()
            } else {
                finish()
            }
        }

        binding.btnClearHistory.setOnClickListener {
            showClearHistoryDialog(false)
        }

        binding.cbSelectAll.setOnClickListener {
            historyAdapter.selectAll()
        }

        binding.actionDelete.setOnClickListener {
            showClearHistoryDialog(true)
        }

        binding.actionCopy.setOnClickListener {
            val selected = historyAdapter.getSelectedItems()
            if (selected.isNotEmpty()) {
                val text = selected.joinToString("\n") { "${it.expression}=${it.result}" }
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("History", text)
                clipboard.setPrimaryClip(clip)
                historyAdapter.toggleSelectionMode()
            }
        }

        binding.actionRecalculate.setOnClickListener {
            val selected = historyAdapter.getSelectedItems()
            if (selected.size == 1) {
                val expr = selected.first().expression
                val intent = Intent()
                intent.putExtra("EXTRA_EXPRESSION", expr)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.history.collect { historyList ->
                        historyAdapter.submitList(historyList)
                    }
                }
            }
        }
    }

    private fun showClearHistoryDialog(isSelectionMode: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_clear_history, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.BOTTOM)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            if (isSelectionMode) {
                val ids = historyAdapter.selectedItemIds.toList()
                viewModel.deleteHistoryItems(ids)
                historyAdapter.toggleSelectionMode()
            } else {
                viewModel.clearHistory()
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}
