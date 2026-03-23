package com.shivam.simplecalculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.shivam.simplecalculator.databinding.ActivityMainBinding
import com.shivam.simplecalculator.ui.CalculatorViewModel
import com.shivam.simplecalculator.services.FloatingCalculatorService
import android.provider.Settings
import android.net.Uri
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    
    private val viewModel: CalculatorViewModel by viewModels()

    private var isScientificMode = false

    private val historyLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        moveTaskToBack(true) // Minimize the app
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
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
            if (Settings.canDrawOverlays(this)) {
                startFloatingService()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
                startActivity(intent)
                Toast.makeText(this, "Please grant permission to resize app", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnHistoryMenu.setOnClickListener {
            historyLauncher.launch(Intent(this, HistoryActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            val intent = android.content.Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btnBackspace.setOnClickListener {
            com.shivam.simplecalculator.util.VibrationUtil.vibrate(this)
            viewModel.backspace()
        }

        // Standard Keyboard
        binding.standardKeyboard.btn0.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("0") }
        binding.standardKeyboard.btn00.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("00") }
        binding.standardKeyboard.btn1.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("1") }
        binding.standardKeyboard.btn2.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("2") }
        binding.standardKeyboard.btn3.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("3") }
        binding.standardKeyboard.btn4.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("4") }
        binding.standardKeyboard.btn5.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("5") }
        binding.standardKeyboard.btn6.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("6") }
        binding.standardKeyboard.btn7.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("7") }
        binding.standardKeyboard.btn8.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("8") }
        binding.standardKeyboard.btn9.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("9") }
        
        binding.standardKeyboard.btnDot.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append(".") }
        binding.standardKeyboard.btnPlus.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("+") }
        binding.standardKeyboard.btnMinus.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("−") }
        binding.standardKeyboard.btnMul.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("×") }
        binding.standardKeyboard.btnDiv.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("÷") }
        binding.standardKeyboard.btnPercent.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("%") }
        
        binding.standardKeyboard.btnAC.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.clear() }
        binding.standardKeyboard.btnEqual.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.calculate() }
        
        var parCount = 0
        binding.standardKeyboard.btnPar.setOnClickListener { 
            com.shivam.simplecalculator.util.VibrationUtil.vibrate(this)
            if (parCount % 2 == 0) {
                viewModel.append("(")
            } else {
                viewModel.append(")")
            }
            parCount++
        }
        
        // Scientific Keyboard Setup (snake_case IDs in XML -> camelCase in Binding)
        val sci = binding.scientificKeyboard
        sci.btn0Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("0") }
        sci.btn1Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("1") }
        sci.btn2Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("2") }
        sci.btn3Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("3") }
        sci.btn4Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("4") }
        sci.btn5Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("5") }
        sci.btn6Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("6") }
        sci.btn7Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("7") }
        sci.btn8Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("8") }
        sci.btn9Sci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("9") }
        
        sci.btnDotSci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append(".") }
        sci.btnPlusSci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("+") }
        sci.btnMinusSci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("−") }
        sci.btnMulSci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("×") }
        sci.btnDivSci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("÷") }
        sci.btnPercentSci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("%") }
        
        sci.btnACSci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.clear() }
        sci.btnEqualSci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.calculate() }
        sci.btnBackspaceSci.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.backspace() }
        
        sci.btnToggle.setOnClickListener { toggleScientificMode() }

        sci.btnParOpen.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("(") }
        sci.btnParClose.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append(")") }
        sci.btnSqrt.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("√") }
        sci.btnPi.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("π") }
        sci.btnE.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("e") }
        sci.btnFact.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("!") }
        sci.btnPower.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("^") }
        sci.btnLog.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("log(") }
        sci.btnLn.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("ln(") }
        sci.btnInvX.setOnClickListener { com.shivam.simplecalculator.util.VibrationUtil.vibrate(this); viewModel.append("1/") }

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
            }
        }
    }
}