package com.shivam.simplecalculator.ui.activites

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.ActivityMainBinding
import com.shivam.simplecalculator.domain.services.FloatingCalculatorService
import com.shivam.simplecalculator.domain.util.ExpressionFormatter
import com.shivam.simplecalculator.domain.util.VibrationUtil
import com.shivam.simplecalculator.ui.viewmodel.CalculatorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: CalculatorViewModel by viewModels()

    private var isScientificMode = false

    private val historyLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val expr = result.data?.getStringExtra("EXTRA_EXPRESSION")
                if (expr != null) {
                    viewModel.clear()
                    viewModel.setExpression(expr)
                }
            }
        }

    private fun startFloatingService() {
        val intent = Intent(this, FloatingCalculatorService::class.java)
        startService(intent)
        moveTaskToBack(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()

        val disablePasteCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean = false
            override fun onDestroyActionMode(mode: ActionMode?) {}
        }

        binding.tvExpression.showSoftInputOnFocus = false
        binding.tvExpression.customSelectionActionModeCallback = disablePasteCallback
        binding.tvExpression.customInsertionActionModeCallback = disablePasteCallback

        binding.tvError.showSoftInputOnFocus = false
        binding.tvError.customSelectionActionModeCallback = disablePasteCallback
        binding.tvError.customInsertionActionModeCallback = disablePasteCallback
    }

    private fun setupListeners() {
        binding.btnMenu.setOnClickListener {
            val intent = Intent(this, OtherCalculatorActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.btnScientific.setOnClickListener {
            toggleScientificMode()
        }

        binding.btnResize.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                startFloatingService()
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:$packageName".toUri()
                )
                startActivity(intent)
                Toast.makeText(
                    this,
                    getString(R.string.please_grant_permission_to_resize_app),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnHistoryMenu.setOnClickListener {
            historyLauncher.launch(Intent(this, HistoryActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btnBackspace.setOnClickListener {
            VibrationUtil.vibrate(this)
            viewModel.backspace(getRawSelection())
        }

        binding.standardKeyboard.btn0.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "0",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn00.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "00",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn1.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "1",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn2.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "2",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn3.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "3",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn4.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "4",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn5.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "5",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn6.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "6",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn7.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "7",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn8.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "8",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btn9.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "9",
            getRawSelection()
        )
        }

        binding.standardKeyboard.btnDot.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            ".",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btnPlus.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "+",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btnMinus.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "−",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btnMul.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "×",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btnDiv.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "÷",
            getRawSelection()
        )
        }
        binding.standardKeyboard.btnPercent.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "%",
            getRawSelection()
        )
        }

        binding.standardKeyboard.btnAC.setOnClickListener { VibrationUtil.vibrate(this); viewModel.clear() }
        binding.standardKeyboard.btnEqual.setOnClickListener { VibrationUtil.vibrate(this); viewModel.calculate() }

        var parCount = 0
        binding.standardKeyboard.btnPar.setOnClickListener {
            VibrationUtil.vibrate(this)
            val pos = getRawSelection()
            if (parCount % 2 == 0) {
                viewModel.append("(", pos)
            } else {
                viewModel.append(")", pos)
            }
            parCount++
        }

        val sci = binding.scientificKeyboard
        sci.btn0.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "0",
            getRawSelection()
        )
        }
        sci.btn1.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "1",
            getRawSelection()
        )
        }
        sci.btn2.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "2",
            getRawSelection()
        )
        }
        sci.btn3.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "3",
            getRawSelection()
        )
        }
        sci.btn4.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "4",
            getRawSelection()
        )
        }
        sci.btn5.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "5",
            getRawSelection()
        )
        }
        sci.btn6.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "6",
            getRawSelection()
        )
        }
        sci.btn7.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "7",
            getRawSelection()
        )
        }
        sci.btn8.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "8",
            getRawSelection()
        )
        }
        sci.btn9.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "9",
            getRawSelection()
        )
        }

        sci.btnDot.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            ".",
            getRawSelection()
        )
        }
        sci.btnPlus.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "+",
            getRawSelection()
        )
        }
        sci.btnMinus.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "−",
            getRawSelection()
        )
        }
        sci.btnMul.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "×",
            getRawSelection()
        )
        }
        sci.btnDiv.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "÷",
            getRawSelection()
        )
        }
        sci.btnPercent.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "%",
            getRawSelection()
        )
        }

        sci.btnAC.setOnClickListener { VibrationUtil.vibrate(this); viewModel.clear() }
        sci.btnEqual.setOnClickListener { VibrationUtil.vibrate(this); viewModel.calculate() }
        sci.btnBackspace.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.backspace(
            getRawSelection()
        )
        }

        sci.btnToggleContainer.setOnClickListener { toggleScientificMode() }

        sci.btnParOpen.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "(",
            getRawSelection()
        )
        }
        sci.btnParClose.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            ")",
            getRawSelection()
        )
        }
        sci.btnSqrt.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "√",
            getRawSelection()
        )
        }
        sci.btnPi.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "π",
            getRawSelection()
        )
        }
        sci.btnE.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "e",
            getRawSelection()
        )
        }
        sci.btnFact.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "!",
            getRawSelection()
        )
        }
        sci.btnPower.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "^",
            getRawSelection()
        )
        }
        sci.btnLog.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "log(",
            getRawSelection()
        )
        }
        sci.btnLn.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "ln(",
            getRawSelection()
        )
        }
        sci.btnInvX.setOnClickListener {
            VibrationUtil.vibrate(this); viewModel.append(
            "1/",
            getRawSelection()
        )
        }

        // Scientific Modes
        sci.btnDeg.setOnClickListener {
            viewModel.toggleDegMode()
            if (viewModel.isDegMode) {
                sci.tvDeg.imageTintList = ColorStateList.valueOf(getColor(R.color.text_color))
            } else {
                sci.tvDeg.imageTintList = ColorStateList.valueOf(getColor(R.color.diable_color))
            }
        }

        sci.btnInv.setOnClickListener {
            viewModel.isInverseMode = !viewModel.isInverseMode
            if (viewModel.isInverseMode) {
                sci.imgSin.setImageResource(R.drawable.ic_sin_minus)
                sci.imgCos.setImageResource(R.drawable.ic_con_minus)
                sci.imgTan.setImageResource(R.drawable.ic_tan_minus)
                sci.imgInv.imageTintList = ColorStateList.valueOf(getColor(R.color.diable_color))
            } else {
                sci.imgSin.setImageResource(R.drawable.ic_sin)
                sci.imgCos.setImageResource(R.drawable.ic_con)
                sci.imgTan.setImageResource(R.drawable.ic_tan)
                sci.imgInv.imageTintList = ColorStateList.valueOf(getColor(R.color.text_color))
            }
        }

        sci.btnSin.setOnClickListener {
            val pos = getRawSelection()
            if (viewModel.isInverseMode) viewModel.append(
                "sin⁻¹(",
                pos
            ) else viewModel.append("sin(", pos)
        }
        sci.btnCos.setOnClickListener {
            val pos = getRawSelection()
            if (viewModel.isInverseMode) viewModel.append(
                "cos⁻¹(",
                pos
            ) else viewModel.append("cos(", pos)
        }
        sci.btnTan.setOnClickListener {
            val pos = getRawSelection()
            if (viewModel.isInverseMode) viewModel.append(
                "tan⁻¹(",
                pos
            ) else viewModel.append("tan(", pos)
        }
    }

    private fun getRawSelection(): Int {
        return ExpressionFormatter.getRawPosition(
            viewModel.expression.value,
            binding.tvExpression.selectionStart
        )
    }

    private fun toggleScientificMode() {
        isScientificMode = !isScientificMode
        if (isScientificMode) {
            binding.standardKeyboard.root.visibility = View.GONE
            binding.scientificKeyboard.root.visibility = View.VISIBLE
            binding.btnBackspace.visibility = View.GONE
        } else {
            binding.btnBackspace.visibility = View.VISIBLE
            binding.scientificKeyboard.root.visibility = View.GONE
            binding.standardKeyboard.root.visibility = View.VISIBLE
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    combine(viewModel.expression, viewModel.selection) { expr, sel ->
                        expr to sel
                    }.collect { (expr, sel) ->
                        val formattedExpr = ExpressionFormatter.format(expr)
                        val formattedSel = ExpressionFormatter.getFormattedPosition(expr, sel)

                        if (binding.tvExpression.text.toString() != formattedExpr) {
                            binding.tvExpression.setText(formattedExpr)
                        }
                        binding.tvExpression.setSelection(
                            formattedSel.coerceIn(
                                0,
                                binding.tvExpression.text?.length ?: 0
                            )
                        )
                    }
                }
                launch {
                    viewModel.result.collect { res ->
                        binding.tvResult.text = res
                        if (res == "Error") {
                            binding.tvResult.setTextColor(Color.RED)
                        } else {
                            binding.tvResult.setTextColor(getColor(R.color.text_color))
                        }
                    }
                }
            }
        }
    }
}