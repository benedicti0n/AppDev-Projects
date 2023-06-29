package com.example.calculator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception




class MainActivity : AppCompatActivity() {

//    private lateinit var blurLayout: BlurLayout

    //for set the custom background
    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()){
        backgroundImage.setImageURI(it)
    }

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
    private lateinit var btnC: Button
    private lateinit var btnHistory: Button
    private lateinit var btnScientific: Button
    private lateinit var btnPercentage: Button
    private lateinit var btnBackspace: Button
    private lateinit var btnDot:Button
    private lateinit var btnPopup: FloatingActionButton
    private lateinit var  backgroundImage:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        backgroundImage = findViewById(R.id.backgroundImage)


//        blurLayout = findViewById(R.id.blurLayout)
//        BlurKit.init(this)

        btnPopup = findViewById(R.id.btnPopup)

        formula = findViewById(R.id.formula)
        output = findViewById(R.id.output)

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
        btnDot = findViewById(R.id.btnDot)
        btnHistory = findViewById(R.id.btnHistory)
        btnScientific = findViewById(R.id.btnScientific)
        btnPercentage = findViewById(R.id.btnPercentage)
        btnBackspace = findViewById(R.id.btnBackspace)



        output.movementMethod = ScrollingMovementMethod()
        output.isActivated = true
        output.isPressed = true


        var str:String





        btnOpeningBracket.setOnClickListener {
//            formula.text = addToInputText("(")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "("
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "("
                expressionText(str)
                showResult()
            }
        }
        btnClosingBracket.setOnClickListener {
//            formula.text = addToInputText(")")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + ")"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + ")"
                expressionText(str)
                showResult()
            }
        }
        btnOne.setOnClickListener {
//            formula.text = addToInputText("1")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "1"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "1"
                expressionText(str)
                showResult()
            }
        }
        btnTwo.setOnClickListener {
//            formula.text = addToInputText("2")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "2"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "2"
                expressionText(str)
                showResult()
            }
        }
        btnThree.setOnClickListener {
//            formula.text = addToInputText("3")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "3"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "3"
                expressionText(str)
                showResult()
            }
        }
        btnFour.setOnClickListener {
//            formula.text = addToInputText("4")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "4"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "4"
                expressionText(str)
                showResult()
            }
        }
        btnFive.setOnClickListener {
//            formula.text = addToInputText("5")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "5"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "5"
                expressionText(str)
                showResult()
            }
        }
        btnSix.setOnClickListener {
//            formula.text = addToInputText("6")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "6"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "6"
                expressionText(str)
                showResult()
            }
        }
        btnSeven.setOnClickListener {
//            formula.text = addToInputText("7")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "7"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "7"
                expressionText(str)
                showResult()
            }
        }
        btnEight.setOnClickListener {
//            formula.text = addToInputText("8")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "8"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "8"
                expressionText(str)
                showResult()
            }
        }
        btnNine.setOnClickListener {
//            formula.text = addToInputText("9")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "9"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "9"
                expressionText(str)
                showResult()
            }
        }
        btnZero.setOnClickListener {
//            formula.text = addToInputText("0")
        if (formula.text.toString().startsWith("0")){
            str = formula.text.toString().replace("0", "") + "0"
            expressionText(str)
            showResult()
        }else{
            str = formula.text.toString() + "0"
            expressionText(str)
            showResult()
        }

        }

        btnDoubleZero.setOnClickListener {
//            formula.text = addToInputText("00")
            if (formula.text.toString().startsWith("0")){
                str = formula.text.toString().replace("0", "") + "00"
                expressionText(str)
                showResult()
            }else{
                str = formula.text.toString() + "00"
                expressionText(str)
                showResult()
            }
        }

        btnPlus.setOnClickListener {
//            formula.text = addToInputText("+")
            if(formula.text.toString().endsWith("%")||formula.text.toString().endsWith("/")||formula.text.toString().endsWith("*")||formula.text.toString().endsWith("+")||formula.text.toString().endsWith("-")||formula.text.toString().endsWith(".")){
                str = formula.text.toString()
                expressionText(str)
            }else   {
                str = formula.text.toString() + "+"
                expressionText(str)
            }
        }
        btnMinus.setOnClickListener {
//            formula.text = addToInputText("-")
            if(formula.text.toString().endsWith("%")||formula.text.toString().endsWith("/")||formula.text.toString().endsWith("*")||formula.text.toString().endsWith("+")||formula.text.toString().endsWith("-")||formula.text.toString().endsWith(".")){
                str = formula.text.toString()
                expressionText(str)
            }else   {
                str = formula.text.toString() + "-"
                expressionText(str)
            }
        }
        btnMultiply.setOnClickListener {
//            formula.text = addToInputText("×")
            if(formula.text.toString().endsWith("%")||formula.text.toString().endsWith("/")||formula.text.toString().endsWith("*")||formula.text.toString().endsWith("+")||formula.text.toString().endsWith("-")||formula.text.toString().endsWith(".")){
                str = formula.text.toString()
                expressionText(str)
            }else   {
                str = formula.text.toString() + "*"
                expressionText(str)
            }
        }
        btnDivide.setOnClickListener {
//            formula.text = addToInputText("/")
            if(formula.text.toString().endsWith("%")||formula.text.toString().endsWith("/")||formula.text.toString().endsWith("*")||formula.text.toString().endsWith("+")||formula.text.toString().endsWith("-")||formula.text.toString().endsWith(".")){
                str = formula.text.toString()
                expressionText(str)
            }else   {
                str = formula.text.toString() + "/"
                expressionText(str)
            }
        }
        btnDot.setOnClickListener {
//            formula.text = addToInputText(".")
            if(formula.text.toString().endsWith("%")||formula.text.toString().endsWith("/")||formula.text.toString().endsWith("*")||formula.text.toString().endsWith("+")||formula.text.toString().endsWith("-")||formula.text.toString().endsWith(".")){
                str = formula.text.toString()
                expressionText(str)
            }else   {
                str = formula.text.toString() + "."
                expressionText(str)
            }
        }
        btnHistory.setOnClickListener {

        }
        btnScientific.setOnClickListener {

        }
        btnPercentage.setOnClickListener {

//            formula.text = addToInputText("%")

            if(formula.text.toString().endsWith("%")||formula.text.toString().endsWith("/")||formula.text.toString().endsWith("*")||formula.text.toString().endsWith("+")||formula.text.toString().endsWith("-")||formula.text.toString().endsWith(".")){
                str = formula.text.toString()
                expressionText(str)
            }else   {
                str = formula.text.toString() + "%"
                expressionText(str)
            }


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


        //Popup Menu

        btnPopup.setOnClickListener {
            val popupMenu = PopupMenu(this, btnPopup)
            popupMenu.menuInflater.inflate(R.menu.example_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                // Handle menu item click events here
                when (menuItem.itemId) {
                    R.id.bg1 -> {

                        backgroundImage.setImageResource(R.drawable.bg4)
                        true
                    }
                    R.id.bg2 -> {

                        backgroundImage.setImageResource(R.drawable.bg2)
                        true
                    }
                    // Add cases for other menu items as needed
                    R.id.bg3 -> {

                        backgroundImage.setImageResource(R.drawable.bg3)
                        true
                    }
                    R.id.bg4 -> {
                        //Custom image pick

                        contract.launch("image/*")

                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }


    }




     private fun expressionText(str: String){
         formula.text = str
     }




//    private fun addToInputText(buttonValue: String): String{
//        return "${formula.text}$buttonValue"
//    }


    private fun getInputExpression(): String {
        return formula.text.replace(Regex("×"), "*")
    }


    private fun showResult() {


        val exp = formula.text.toString()

        try {
            val expression = ExpressionBuilder(exp).build()
            val result = expression.evaluate()
            val formattedResult = if (result % 1 == 0.0) {
                result.toLong().toString()
            } else {
                result.toString()
            }
            output.text = "$formattedResult"
        } catch (e: Exception) {
            formula.text = formula.text.toString()
            output.text = formula.text.toString()
        }

//        try {
//            val expression = getInputExpression()
//            val result = Expression(expression).calculate()
//            if (result.isNaN()){
//                //show error message
//                output.text = "Error"
////
//            }else{
//                //show Result
//                output.text = DecimalFormat("0.######").format(result).toString()
////
//            }
//        }catch (_: Exception){
//            //show error message
//            output.text = "Error"
////
//        }

    }
    
    


//
//    override fun onStart() {
//        super.onStart()
//        blurLayout.startBlur()
//    }
//
//    override fun onStop() {
//        blurLayout.pauseBlur()
//        super.onStop()
//    }



}
