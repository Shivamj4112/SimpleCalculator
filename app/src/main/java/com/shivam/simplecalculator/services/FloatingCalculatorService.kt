package com.shivam.simplecalculator.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.TextView
import android.graphics.Color
import android.widget.ImageView
import com.shivam.simplecalculator.MainActivity
import com.shivam.simplecalculator.R
import com.shivam.simplecalculator.databinding.LayoutFloatingCalculatorBinding
import com.shivam.simplecalculator.util.CalculatorEngine
import com.shivam.simplecalculator.util.ExpressionManager
import com.shivam.simplecalculator.data.HistoryRepository
import com.shivam.simplecalculator.data.CalculationHistory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class FloatingCalculatorService : Service() {

    @Inject
    lateinit var repository: HistoryRepository

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var binding: LayoutFloatingCalculatorBinding
    
    private val expressionManager = ExpressionManager()
    private var resultDisplay = ""
    private var isCalculated = false

    private val formatter = DecimalFormat("#.##########")
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = inflater.inflate(R.layout.layout_floating_calculator, null)
        binding = LayoutFloatingCalculatorBinding.bind(floatingView)

        val density = resources.displayMetrics.density
        val widthPx = (360 * density).toInt()

        val params = WindowManager.LayoutParams(
            widthPx,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 100

        windowManager.addView(floatingView, params)

        setupListeners(params)
    }

    private fun setupListeners(params: WindowManager.LayoutParams) {
        // Dragging logic
        binding.header.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingView, params)
                        return true
                    }
                }
                return false
            }
        })

        binding.btnClose.setOnClickListener {
            stopSelf()
        }

        binding.btnExpand.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            stopSelf()
        }

        binding.btnClose.setImageResource(R.drawable.ic_close_picker)

        val pad = binding.layoutStandard
        
        pad.btn0.setOnClickListener { append("0") }
        pad.btn00.setOnClickListener { append("00") }
        pad.btn1.setOnClickListener { append("1") }
        pad.btn2.setOnClickListener { append("2") }
        pad.btn3.setOnClickListener { append("3") }
        pad.btn4.setOnClickListener { append("4") }
        pad.btn5.setOnClickListener { append("5") }
        pad.btn6.setOnClickListener { append("6") }
        pad.btn7.setOnClickListener { append("7") }
        pad.btn8.setOnClickListener { append("8") }
        pad.btn9.setOnClickListener { append("9") }
        
        pad.btnDot.setOnClickListener { append(".") }
        pad.btnPlus.setOnClickListener { append("+") }
        pad.btnMinus.setOnClickListener { append("−") }
        pad.btnMul.setOnClickListener { append("×") }
        pad.btnDiv.setOnClickListener { append("÷") }
        pad.btnPercent.setOnClickListener { append("%") }
        
        pad.btnAC.setOnClickListener { clear() }
        pad.btnEqual.setOnClickListener { calculate() }
        
        // Handling parenthese logic correctly from generic button
        var parCount = 0
        pad.btnPar.setOnClickListener { 
            if (parCount % 2 == 0) append("(") else append(")")
            parCount++
            // Note: expression Manager handles multiple ( and ) efficiently too!
        }
    }

    private fun append(char: String) {
        if (resultDisplay == "Invalid Expression" || resultDisplay == "Cannot divide by zero") {
            resultDisplay = ""
        }
        
        if (isCalculated) {
            if (!char.matches(Regex("[+\\-×÷%^]"))) {
                expressionManager.clear()
            } else {
                expressionManager.setExpression(resultDisplay.replace(",", ""))
            }
            isCalculated = false
        }

        expressionManager.append(char)
        updateRealTimeResult()
    }

    private fun clear() {
        expressionManager.clear()
        resultDisplay = ""
        isCalculated = false
        updateDisplay()
    }

    private fun updateRealTimeResult() {
        if (expressionManager.expression.isEmpty()) {
            resultDisplay = ""
            updateDisplay()
            return
        }
        
        val resultAttempt = CalculatorEngine.evaluate(expressionManager.expression, true)
        if (resultAttempt.isSuccess) {
            val res = resultAttempt.getOrNull() ?: 0.0
            resultDisplay = formatter.format(res)
        } else {
            resultDisplay = ""
        }
        updateDisplay()
    }

    private fun calculate() {
        if (expressionManager.expression.isEmpty()) return
        
        val resultAttempt = CalculatorEngine.evaluate(expressionManager.expression, true)
        if (resultAttempt.isSuccess) {
            val res = resultAttempt.getOrNull() ?: 0.0
            resultDisplay = formatter.format(res)
            
            isCalculated = true
            
            expressionManager.setExpression(autoCloseBrackets(expressionManager.expression))
            
            serviceScope.launch {
                repository.insert(CalculationHistory(
                    expression = expressionManager.expression,
                    result = resultDisplay
                ))
            }
        } else {
            val error = resultAttempt.exceptionOrNull()
            resultDisplay = error?.message ?: "Invalid Expression"
            isCalculated = true
        }
        updateDisplay()
    }
    
    private fun autoCloseBrackets(expr: String): String {
        var newExpr = expr
        val openCount = newExpr.count { it == '(' }
        val closeCount = newExpr.count { it == ')' }
        if (openCount > closeCount) {
            newExpr += ")".repeat(openCount - closeCount)
        }
        return newExpr
    }

    private fun updateDisplay() {
        binding.tvExpression.text = expressionManager.expression
        binding.tvResult.text = resultDisplay
        
        if (resultDisplay == "Invalid Expression" || resultDisplay == "Cannot divide by zero") {
            binding.tvResult.setTextColor(Color.RED)
        } else {
            binding.tvResult.setTextColor(Color.BLACK)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}
