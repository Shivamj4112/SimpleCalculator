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
                viewModel.append(expr)
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
            }
        }
    }
}