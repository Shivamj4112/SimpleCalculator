package com.shivam.simplecalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.shivam.simplecalculator.databinding.ActivityMainBinding
import com.shivam.simplecalculator.ui.CalculatorViewModel
import com.shivam.simplecalculator.ui.HistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    
    private val viewModel: CalculatorViewModel by viewModels()

    private lateinit var historyAdapter: HistoryAdapter
    private var isScientificMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(
            onItemClick = { history ->
                binding.historyView.root.visibility = View.GONE
            },
            onItemLongClick = {
                // Handled in adapter, selection mode toggled
            },
            onSelectionChange = { count ->
                updateHistorySelectionUI(count)
            }
        )
        binding.historyView.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = historyAdapter
        }
    }

    private fun updateHistorySelectionUI(count: Int) {
        if (historyAdapter.isSelectionMode) {
            binding.historyView.tvHistoryTitle.text = if (count > 0) getString(
                R.string.selected,
                count
            ) else getString(R.string.select_items)
            binding.historyView.cbSelectAll.visibility = View.VISIBLE
            binding.historyView.btnClearHistory.visibility = View.GONE
            binding.historyView.selectionActionBar.visibility = View.VISIBLE
            
            // Update Checkbox state
            binding.historyView.cbSelectAll.isChecked = count > 0 && count == historyAdapter.currentList.size
        } else {
            binding.historyView.tvHistoryTitle.text = getString(R.string.history)
            binding.historyView.cbSelectAll.visibility = View.GONE
            binding.historyView.cbSelectAll.isChecked = false
            binding.historyView.btnClearHistory.visibility = View.VISIBLE
            binding.historyView.selectionActionBar.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        // Mode Switches
        binding.btnMenu.setOnClickListener {
            val intent = android.content.Intent(this, OtherCalculatorActivity::class.java)
            startActivity(intent)
        }

        binding.btnScientific.setOnClickListener {
            toggleScientificMode()
        }

        binding.btnResize.setOnClickListener {
        }

        binding.btnHistoryMenu.setOnClickListener {
            binding.historyView.root.visibility = View.VISIBLE
        }

        binding.btnSettings.setOnClickListener {
            val intent = android.content.Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.historyView.btnCloseHistory.setOnClickListener {
            if (historyAdapter.isSelectionMode) {
                historyAdapter.toggleSelectionMode()
            } else {
                binding.historyView.root.visibility = View.GONE
            }
        }

        binding.historyView.btnClearHistory.setOnClickListener {
            showClearHistoryDialog(false)
        }

        binding.historyView.cbSelectAll.setOnClickListener {
            historyAdapter.selectAll()
        }

        binding.historyView.actionDelete.setOnClickListener {
            showClearHistoryDialog(true)
        }

        binding.historyView.actionCopy.setOnClickListener {
            val selected = historyAdapter.getSelectedItems()
            if (selected.isNotEmpty()) {
                val text = selected.joinToString("\n") { "${it.expression}=${it.result}" }
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("History", text)
                clipboard.setPrimaryClip(clip)
                historyAdapter.toggleSelectionMode()
            }
        }

        binding.historyView.actionRecalculate.setOnClickListener {
            val selected = historyAdapter.getSelectedItems()
            if (selected.size == 1) {
                viewModel.clear()
                viewModel.append(selected.first().expression)
                historyAdapter.toggleSelectionMode()
                binding.historyView.root.visibility = View.GONE
            }
        }

        binding.btnBackspace.setOnClickListener {
            viewModel.backspace()
        }

        // Standard Keyboard
        binding.standardKeyboard.btn0.setOnClickListener { viewModel.append("0") }
        binding.standardKeyboard.btn00.setOnClickListener { viewModel.append("00") }
        binding.standardKeyboard.btn1.setOnClickListener { viewModel.append("1") }
        binding.standardKeyboard.btn2.setOnClickListener { viewModel.append("2") }
        binding.standardKeyboard.btn3.setOnClickListener { viewModel.append("3") }
        binding.standardKeyboard.btn4.setOnClickListener { viewModel.append("4") }
        binding.standardKeyboard.btn5.setOnClickListener { viewModel.append("5") }
        binding.standardKeyboard.btn6.setOnClickListener { viewModel.append("6") }
        binding.standardKeyboard.btn7.setOnClickListener { viewModel.append("7") }
        binding.standardKeyboard.btn8.setOnClickListener { viewModel.append("8") }
        binding.standardKeyboard.btn9.setOnClickListener { viewModel.append("9") }
        
        binding.standardKeyboard.btnDot.setOnClickListener { viewModel.append(".") }
        binding.standardKeyboard.btnPlus.setOnClickListener { viewModel.append("+") }
        binding.standardKeyboard.btnMinus.setOnClickListener { viewModel.append("−") }
        binding.standardKeyboard.btnMul.setOnClickListener { viewModel.append("×") }
        binding.standardKeyboard.btnDiv.setOnClickListener { viewModel.append("÷") }
        binding.standardKeyboard.btnPercent.setOnClickListener { viewModel.append("%") }
        
        binding.standardKeyboard.btnAC.setOnClickListener { viewModel.clear() }
        binding.standardKeyboard.btnEqual.setOnClickListener { viewModel.calculate() }
        
        var parCount = 0
        binding.standardKeyboard.btnPar.setOnClickListener { 
            if (parCount % 2 == 0) {
                viewModel.append("(")
            } else {
                viewModel.append(")")
            }
            parCount++
        }
        
        // Scientific Keyboard Setup (snake_case IDs in XML -> camelCase in Binding)
        val sci = binding.scientificKeyboard
        sci.btn0Sci.setOnClickListener { viewModel.append("0") }
        sci.btn1Sci.setOnClickListener { viewModel.append("1") }
        sci.btn2Sci.setOnClickListener { viewModel.append("2") }
        sci.btn3Sci.setOnClickListener { viewModel.append("3") }
        sci.btn4Sci.setOnClickListener { viewModel.append("4") }
        sci.btn5Sci.setOnClickListener { viewModel.append("5") }
        sci.btn6Sci.setOnClickListener { viewModel.append("6") }
        sci.btn7Sci.setOnClickListener { viewModel.append("7") }
        sci.btn8Sci.setOnClickListener { viewModel.append("8") }
        sci.btn9Sci.setOnClickListener { viewModel.append("9") }
        
        sci.btnDotSci.setOnClickListener { viewModel.append(".") }
        sci.btnPlusSci.setOnClickListener { viewModel.append("+") }
        sci.btnMinusSci.setOnClickListener { viewModel.append("−") }
        sci.btnMulSci.setOnClickListener { viewModel.append("×") }
        sci.btnDivSci.setOnClickListener { viewModel.append("÷") }
        sci.btnPercentSci.setOnClickListener { viewModel.append("%") }
        
        sci.btnACSci.setOnClickListener { viewModel.clear() }
        sci.btnEqualSci.setOnClickListener { viewModel.calculate() }
        sci.btnBackspaceSci.setOnClickListener { viewModel.backspace() }
        
        sci.btnToggle.setOnClickListener { toggleScientificMode() }

        sci.btnParOpen.setOnClickListener { viewModel.append("(") }
        sci.btnParClose.setOnClickListener { viewModel.append(")") }
        sci.btnSqrt.setOnClickListener { viewModel.append("√") }
        sci.btnPi.setOnClickListener { viewModel.append("π") }
        sci.btnE.setOnClickListener { viewModel.append("e") }
        sci.btnFact.setOnClickListener { viewModel.append("!") }
        sci.btnPower.setOnClickListener { viewModel.append("^") }
        sci.btnLog.setOnClickListener { viewModel.append("log(") }
        sci.btnLn.setOnClickListener { viewModel.append("ln(") }
        sci.btnInvX.setOnClickListener { viewModel.append("1/") }

        // Scientific Modes
        sci.btnDeg.setOnClickListener {
            viewModel.isDegMode = !viewModel.isDegMode
            sci.btnDeg.text = if (viewModel.isDegMode) "deg" else "rad"
        }

        sci.btnInv.setOnClickListener {
            viewModel.isInverseMode = !viewModel.isInverseMode
            if (viewModel.isInverseMode) {
                sci.btnSin.text = "sin-1"
                sci.btnCos.text = "cos-1"
                sci.btnTan.text = "tan-1"
                sci.btnInv.setBackgroundColor(android.graphics.Color.LTGRAY)
            } else {
                sci.btnSin.text = "sin"
                sci.btnCos.text = "cos"
                sci.btnTan.text = "tan"
                sci.btnInv.setBackgroundColor(android.graphics.Color.WHITE)
            }
        }

        sci.btnSin.setOnClickListener {
            if (viewModel.isInverseMode) viewModel.append("asin(") else viewModel.append("sin(")
        }
        sci.btnCos.setOnClickListener {
            if (viewModel.isInverseMode) viewModel.append("acos(") else viewModel.append("cos(")
        }
        sci.btnTan.setOnClickListener {
            if (viewModel.isInverseMode) viewModel.append("atan(") else viewModel.append("tan(")
        }
    }

    private fun toggleScientificMode() {
        isScientificMode = !isScientificMode
        if (isScientificMode) {
            binding.standardKeyboard.root.visibility = View.GONE
            binding.scientificKeyboard.root.visibility = View.VISIBLE
        } else {
            binding.scientificKeyboard.root.visibility = View.GONE
            binding.standardKeyboard.root.visibility = View.VISIBLE
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.expression.collect { expr ->
                        binding.tvExpression.setText(expr)
                    }
                }
                launch {
                    viewModel.result.collect { res ->
                        binding.tvResult.text = res
                        if (res == "Input Error") {
                            binding.tvResult.setTextColor(android.graphics.Color.RED)
                        } else {
                            binding.tvResult.setTextColor(android.graphics.Color.BLACK)
                        }
                    }
                }
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