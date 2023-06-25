package com.example.calculator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.alterac.blurkit.BlurLayout
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat



class MainActivity : AppCompatActivity() {

    private lateinit var blurLayout: BlurLayout

    private lateinit var formula: TextView
    private lateinit var output: TextView

    private lateinit var btnOpeningBracket: Button
    private lateinit var btnClosingBracket: Button
    private lateinit var btnPlus: Button
    private lateinit var btnMinus: Button
    private lateinit var btnDivide: Button
    private lateinit var btnMultiply: Button
    private lateinit var btnEqual: Button
    private lateinit var btnZero: Button
    private lateinit var btnDoubleZero: Button
    private lateinit var btnOne: Button
    private lateinit var btnTwo: Button
    private lateinit var btnThree: Button
    private lateinit var btnFour: Button
    private lateinit var btnFive: Button
    private lateinit var btnSix: Button
    private lateinit var btnSeven: Button
    private lateinit var btnEight: Button
    private lateinit var btnNine: Button
    private lateinit var btnPoint: Button
    private lateinit var btnC: Button
    private lateinit var btnHistory: Button
    private lateinit var btnScientific: Button
    private lateinit var btnPercentage: Button
    private lateinit var btnBackspace: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        blurLayout = findViewById(R.id.blurLayout)

        formula = findViewById(R.id.formula)
        output = findViewById(R.id.output)

        btnPoint = findViewById(R.id.btnPoint)
        btnOne = findViewById(R.id.btnOne)
        btnTwo = findViewById(R.id.btnTwo)
        btnThree = findViewById(R.id.btnThree)
        btnFour = findViewById(R.id.btnFour)
        btnFive = findViewById(R.id.btnFive)
        btnSix = findViewById(R.id.btnSix)
        btnSeven = findViewById(R.id.btnSeven)
        btnEight = findViewById(R.id.btnEight)
        btnNine = findViewById(R.id.btnNine)
        btnZero = findViewById(R.id.btnZero)
        btnDoubleZero = findViewById(R.id.btnDoubleZero)
        btnPlus = findViewById(R.id.btnPlus)
        btnMinus = findViewById(R.id.btnMinus)
        btnDivide = findViewById(R.id.btnDivide)
        btnMultiply = findViewById(R.id.btnMultiply)
        btnEqual = findViewById(R.id.btnEqual)
        btnOpeningBracket = findViewById(R.id.btnOpeningBracket)
        btnClosingBracket = findViewById(R.id.btnClosingBracket)
        btnC = findViewById(R.id.btnC)
        btnHistory = findViewById(R.id.btnHistory)
        btnScientific = findViewById(R.id.btnScientific)
        btnPercentage = findViewById(R.id.btnPercentage)
        btnBackspace = findViewById(R.id.btnBackspace)



        output.movementMethod = ScrollingMovementMethod()
        output.isActivated = true
        output.isPressed = true


        var str:String



        btnOpeningBracket.setOnClickListener {
            formula.text = addToInputText("(")
        }
        btnClosingBracket.setOnClickListener {
            formula.text = addToInputText(")")
        }
        btnOne.setOnClickListener {
            formula.text = addToInputText("1")
        }
        btnTwo.setOnClickListener {
            formula.text = addToInputText("2")
        }
        btnThree.setOnClickListener {
            formula.text = addToInputText("3")
        }
        btnFour.setOnClickListener {
            formula.text = addToInputText("4")
        }
        btnFive.setOnClickListener {
            formula.text = addToInputText("5")
        }
        btnSix.setOnClickListener {
            formula.text = addToInputText("6")
        }
        btnSeven.setOnClickListener {
            formula.text = addToInputText("7")
        }
        btnEight.setOnClickListener {
            formula.text = addToInputText("8")
        }
        btnNine.setOnClickListener {
            formula.text = addToInputText("9")
        }
        btnZero.setOnClickListener {
            formula.text = addToInputText("0")
        }
        btnDoubleZero.setOnClickListener {
            formula.text = addToInputText("00")
        }
        btnPlus.setOnClickListener {
            formula.text = addToInputText("+")
        }
        btnMinus.setOnClickListener {
            formula.text = addToInputText("-")
        }
        btnMultiply.setOnClickListener {
            formula.text = addToInputText("X")
        }
        btnDivide.setOnClickListener {
            formula.text = addToInputText("/")
        }
        btnHistory.setOnClickListener {

        }
        btnScientific.setOnClickListener {

        }
        btnPercentage.setOnClickListener {

            formula.text = addToInputText("%")

//            if(formula.text.toString().endsWith("%")||formula.text.toString().endsWith("/")||formula.text.toString().endsWith("*")||formula.text.toString().endsWith("+")||formula.text.toString().endsWith("-")||formula.text.toString().endsWith(".")){
//                str = formula.text.toString()
//                expressionText(str)
//            }else   {
//                str = formula.text.toString() + "%"
//                expressionText(str)
//            }
        }
        btnBackspace.setOnClickListener {
            if (formula.text.toString().isNotEmpty()){
                val lastIndex = formula.text.toString().lastIndex
                str = formula.text.toString().substring(0,lastIndex)
                expressionText(str)
                showResult()
            }
        }
        btnC.setOnClickListener {
            formula.text = ""
            output.text = "0"
        }
        btnEqual.setOnClickListener {
            showResult()
        }


    }


    private fun addToInputText(buttonValue: String): String{
        return "${formula.text}$buttonValue"
    }


    private fun getInputExpression(): String {
        return formula.text.replace(Regex("X"), "*")
    }


    private fun expressionText(str:String){
        formula.text = str
    }

    private fun showResult() {

        try {
            val expression = getInputExpression()
            val result = Expression(expression).calculate()
            if (result.isNaN()){
                //show error message
                output.text = "Error"
//                output.setTextColor(ContextCompat.getColor(this, R.color.red))
            }else{
                //show Result
                output.text = DecimalFormat("0.######").format(result).toString()
//                output.setTextColor(ContextCompat.getColor(this, R.color.green))
            }
        }catch (_: Exception){
            //show error message
            output.text = "Error"
//                output.setTextColor(ContextCompat.getColor(this, R.color.red))
        }

    }



    override fun onStart() {
        super.onStart()
        blurLayout.startBlur()
    }

    override fun onStop() {
        blurLayout.pauseBlur()
        super.onStop()
    }

}
