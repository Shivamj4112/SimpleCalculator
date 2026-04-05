package com.shivam.simplecalculator.ui.activites

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityHistoryBinding
import com.shivam.simplecalculator.ui.adapter.HistoryAdapter
import com.shivam.simplecalculator.ui.viewmodel.CalculatorViewModel
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
            onItemClick = { item ->
                sendExpressionAndFinish(item.expression)
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
            
            val isAllSelected = count > 0 && count == historyAdapter.currentList.size
            binding.cbSelectAll.setImageResource(
                if (isAllSelected) R.drawable.ic_checked else R.drawable.ic_unchecked
            )
        } else {
            binding.tvHistoryTitle.text = getString(R.string.history)
            binding.cbSelectAll.visibility = View.GONE
            binding.cbSelectAll.setImageResource(R.drawable.ic_unchecked)
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
                sendExpressionAndFinish(selected.first().expression)
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
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
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

    private fun sendExpressionAndFinish(expression: String) {
        val intent = Intent()
        intent.putExtra("EXTRA_EXPRESSION", expression)
        setResult(RESULT_OK, intent)
        finish()
    }
}
