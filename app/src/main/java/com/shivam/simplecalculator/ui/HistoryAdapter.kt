package com.shivam.simplecalculator.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shivam.simplecalculator.data.CalculationHistory
import com.shivam.simplecalculator.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val onItemClick: (CalculationHistory) -> Unit,
    private val onItemLongClick: () -> Unit,
    private val onSelectionChange: (Int) -> Unit
) : ListAdapter<CalculationHistory, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    var isSelectionMode = false
    val selectedItemIds = mutableSetOf<Long>()

    fun toggleSelectionMode() {
        isSelectionMode = !isSelectionMode
        if (!isSelectionMode) {
            selectedItemIds.clear()
        }
        notifyDataSetChanged()
        onSelectionChange(selectedItemIds.size)
    }

    fun selectAll() {
        if (isSelectionMode) {
            val allIds = currentList.map { it.id }
            if (selectedItemIds.size == allIds.size) {
                selectedItemIds.clear()
            } else {
                selectedItemIds.addAll(allIds)
            }
            notifyDataSetChanged()
            onSelectionChange(selectedItemIds.size)
        }
    }

    fun getSelectedItems(): List<CalculationHistory> {
        return currentList.filter { selectedItemIds.contains(it.id) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: CalculationHistory) {
            binding.tvHistExpression.text = history.expression
            binding.tvHistResult.text = "=${history.result}"

            if (isSelectionMode) {
                binding.cbHistorySelect.visibility = View.VISIBLE
                binding.cbHistorySelect.isChecked = selectedItemIds.contains(history.id)
            } else {
                binding.cbHistorySelect.visibility = View.GONE
                binding.cbHistorySelect.isChecked = false
            }

            binding.root.setOnClickListener {
                if (isSelectionMode) {
                    toggleSelection(history.id)
                } else {
                    onItemClick(history)
                }
            }

            binding.root.setOnLongClickListener {
                if (!isSelectionMode) {
                    toggleSelectionMode()
                    toggleSelection(history.id)
                    onItemLongClick()
                }
                true
            }

            binding.cbHistorySelect.setOnClickListener {
                toggleSelection(history.id)
            }
        }

        private fun toggleSelection(id: Long) {
            if (selectedItemIds.contains(id)) {
                selectedItemIds.remove(id)
            } else {
                selectedItemIds.add(id)
            }
            binding.cbHistorySelect.isChecked = selectedItemIds.contains(id)
            onSelectionChange(selectedItemIds.size)
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<CalculationHistory>() {
        override fun areItemsTheSame(oldItem: CalculationHistory, newItem: CalculationHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CalculationHistory, newItem: CalculationHistory): Boolean {
            return oldItem == newItem
        }
    }
}
