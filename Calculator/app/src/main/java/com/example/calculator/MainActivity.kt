package com.example.calculator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import android.widget.Button
import android.widget.PopupMenu

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.InputStream
import java.io.OutputStream
import android.Manifest
import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.HorizontalScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calculator.databinding.ActivityMainBinding
import com.sothree.slidinguppanel.PanelSlideListener
import com.sothree.slidinguppanel.PanelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.*


class MainActivity : AppCompatActivity() {

//    private lateinit var blurLayout: BlurLayout


    //    for set the custom background
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // Image Uri will not be null for RESULT_OK
                val uri: Uri = data?.data!!

                // Set the image to the ImageView
                binding.backgroundImage.setImageURI(uri)

                // Save the image permanently
                saveImageToStorage(uri)
            } else if (result.resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1
    private var savedImageUri: Uri? = null


//    private lateinit var view: View

    private val decimalSeparatorSymbol =
        DecimalFormatSymbols.getInstance().decimalSeparator.toString()
    private val groupingSeparatorSymbol =
        DecimalFormatSymbols.getInstance().groupingSeparator.toString()

    private var isInvButtonClicked = false
    private var isEqualLastAction = false
    private var isDegreeModeActivated = true // Set degree by default
    private var errorStatusOld = false

    private var calculationResult = BigDecimal.ZERO

    private lateinit var binding: ActivityMainBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyLayoutMgr: LinearLayoutManager


//    private lateinit var btnPopup: ImageButton
//    private lateinit var backgroundImage: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        backgroundImage = findViewById(R.id.backgroundImage)
//        val backgroundImage = findViewById(R.id.backgroundImage)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        setBackgroundImage()


        // Disable the keyboard on display EditText
        binding.formula.showSoftInputOnFocus = false


        binding.btnBackspace.setOnLongClickListener {
            binding.formula.setText("")
            binding.output.text = ""
            true
        }


        // Set default animations and disable the fade out default animation
        val lt = LayoutTransition()
        lt.disableTransitionType(LayoutTransition.DISAPPEARING)
        binding.tableLayout.layoutTransition = lt



        // Set history
        historyLayoutMgr = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.historyRecylcleView.layoutManager = historyLayoutMgr
        historyAdapter = HistoryAdapter(mutableListOf()) { value ->
            run {
                //val valueUpdated = value.replace(".", NumberFormatter.decimalSeparatorSymbol)
                updateDisplay(window.decorView, value)
            }
        }
        binding.historyRecylcleView.adapter = historyAdapter
        // Set values
        val historyList = MyPreferences(this).getHistory()
        historyAdapter.appendHistory(historyList)
        // Scroll to the bottom of the recycle view
        if (historyAdapter.itemCount > 0) {
            binding.historyRecylcleView.scrollToPosition(historyAdapter.itemCount - 1)
        }

        binding.slidingLayout.addPanelSlideListener(object : PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                if (slideOffset == 0f) { // If the panel got collapsed
                    binding.slidingLayout.scrollableView = binding.historyRecylcleView
                }
            }

            override fun onPanelStateChanged(
                panel: View,
                previousState: PanelState,
                newState: PanelState
            ) {
                if (newState == PanelState.ANCHORED) { // To prevent the panel from getting stuck in the middle
                    binding.slidingLayout.panelState = PanelState.EXPANDED
                }
            }
        })



        // Focus by default
        binding.formula.requestFocus()

        // Makes the input take the whole width of the screen by default
        val screenWidthPX = resources.displayMetrics.widthPixels
        binding.formula.minWidth =
            screenWidthPX - (binding.formula.paddingRight + binding.formula.paddingLeft) // remove the paddingHorizontal

        // Do not clear after equal button if you move the cursor
        binding.formula.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun sendAccessibilityEvent(host: View, eventType: Int) {
                super.sendAccessibilityEvent(host, eventType)
                if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                    isEqualLastAction = false
                }
                if (!binding.formula.isCursorVisible) {
                    binding.formula.isCursorVisible = true
                }
            }
        }

        // LongClick on result to copy it
        binding.output.setOnLongClickListener {
            when {
                binding.output.text.toString() != "" -> {
                    if (MyPreferences(this).longClickToCopyValue) {
                        val clipboardManager =
                            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        clipboardManager.setPrimaryClip(
                            ClipData.newPlainText(
                                "Copied result",
                                binding.output.text
                            )
                        )
                        // Only show a toast for Android 12 and lower.
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                            Toast.makeText(this, R.string.value_copied, Toast.LENGTH_SHORT).show()
                        true
                    } else {
                        false
                    }
                }

                else -> false
            }
        }

        // Handle changes into input to update resultDisplay
        binding.formula.addTextChangedListener(object : TextWatcher {
            private var beforeTextLength = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeTextLength = s?.length ?: 0
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateResultDisplay()
                /*val afterTextLength = s?.length ?: 0
                // If the afterTextLength is equals to 0 we have to clear resultDisplay
                if (afterTextLength == 0) {
                    binding.resultDisplay.setText("")
                }

                /* we check if the length of the text entered into the EditText
                is greater than the length of the text before the change (beforeTextLength)
                by more than 1 character. If it is, we assume that this is a paste event. */
                val clipData = clipboardManager.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    //val clipText = clipData.getItemAt(0).coerceToText(this@MainActivity).toString()

                    if (s != null) {
                        //val newValue = s.subSequence(start, start + count).toString()
                        if (
                            (afterTextLength - beforeTextLength > 1)
                            // Removed to avoid anoying notification (https://developer.android.com/develop/ui/views/touch-and-input/copy-paste#PastingSystemNotifications)
                            //|| (afterTextLength - beforeTextLength >= 1 && clipText == newValue) // Supports 1+ new caractere if it is equals to the latest element from the clipboard
                        ) {
                            // Handle paste event here
                            updateResultDisplay()
                        }
                    }
                }*/
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

//        blurLayout = findViewById(R.id.blurLayout)
//        BlurKit.init(this)

//        val btnPopup = binding.btnPopup

//        formula = findViewById(R.id.formula)
//        output = findViewById(R.id.output)

//        btnOne = findViewById(R.id.btnOne)
//        btnTwo = findViewById(R.id.btnTwo)
//        btnThree = findViewById(R.id.btnThree)
//        btnFour = findViewById(R.id.btnFour)
//        btnFive = findViewById(R.id.btnFive)
//        btnSix = findViewById(R.id.btnSix)
//        btnSeven = findViewById(R.id.btnSeven)
//        btnEight = findViewById(R.id.btnEight)
//        btnNine = findViewById(R.id.btnNine)
//        btnZero = findViewById(R.id.btnZero)
////        btnDoubleZero = findViewById(R.id.btnDoubleZero)
//        btnPlus = findViewById(R.id.btnPlus)
//        btnMinus = findViewById(R.id.btnMinus)
//        btnDivide = findViewById(R.id.btnDivide)
//        btnMultiply = findViewById(R.id.btnMultiply)
//        btnEqual = findViewById(R.id.btnEqual)
//        btnOpeningBracket = findViewById(R.id.leftParenthesisButton)
//        btnClosingBracket = findViewById(R.id.rightParenthesisButton)
//        btnC = findViewById(R.id.btnC)
//        btnDot = findViewById(R.id.btnDot)
//        btnPercentage = findViewById(R.id.divideBy100Button)
//        btnBackspace = findViewById(R.id.btnBackspace)


        //Popup Menu
        binding.btnPopup.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.btnPopup)
            popupMenu.menuInflater.inflate(R.menu.example_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                // Handle menu item click events here
                when (menuItem.itemId) {
                    R.id.bg1 -> {
                        // Set background image from drawable
                        saveImageToStorageDrawable(R.drawable.bg1)
                        binding.backgroundImage.setImageResource(R.drawable.bg1)
                        true
                    }

                    R.id.bg2 -> {

                        saveImageToStorageDrawable(R.drawable.bg3)
                        binding.backgroundImage.setImageResource(R.drawable.bg3)
                        true
                    }
                    // Add cases for other menu items as needed
                    R.id.bg3 -> {

                        saveImageToStorageDrawable(R.drawable.bg2)
                        binding.backgroundImage.setImageResource(R.drawable.bg2)
                        true
                    }

                    R.id.bg4 -> {
                        //Custom image pick

                        launchImagePicker()

                        true
                    }

                    R.id.clearHistory -> {

                        clearHistory()

                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }




    }











    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            imagePickerLauncher.launch(intent)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_REQUEST_CODE
            )
        }


    }

    private fun setBackgroundImage() {
        val outputDir: File = getDir("images", Context.MODE_PRIVATE)
        val files: Array<File>? = outputDir.listFiles()

        if (files != null && files.isNotEmpty()) {
            // Sort the files by modification date in descending order
            files.sortByDescending { it.lastModified() }

            val savedImageUri: Uri = Uri.fromFile(files[0])
            binding.backgroundImage.setImageURI(savedImageUri)
        }
    }

    private fun saveImageToStorageDrawable(imageResId: Int) {
        val inputStream: InputStream? = resources.openRawResource(imageResId)

        try {
            val outputDir: File = getDir("images", Context.MODE_PRIVATE)
            val fileName = "profile_image.jpg" // Fixed file name for the profile image
            val outputFile = File(outputDir, fileName)

            // Delete existing file if it exists
            if (outputFile.exists()) {
                outputFile.delete()
            }

            val outputStream: OutputStream = FileOutputStream(outputFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output) // Copy the input stream to the output stream
                }
            }

            savedImageUri = Uri.fromFile(outputFile)

            // Set the image to the ImageView
            binding.backgroundImage.setImageURI(savedImageUri)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
    }

    // Save the image to permanent storage
    private fun saveImageToStorage(uri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        try {
            val outputDir: File = getDir("images", Context.MODE_PRIVATE)
            val fileName = "profile_image.jpg" // Fixed file name for the profile image
            val outputFile = File(outputDir, fileName)

            // Delete existing file if it exists
            if (outputFile.exists()) {
                outputFile.delete()
            }

            val outputStream: OutputStream = FileOutputStream(outputFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output) // Copy the input stream to the output stream
                }
            }


            val savedImageUri: Uri = Uri.fromFile(outputFile)

            // Set the image to the ImageView
            binding.backgroundImage.setImageURI(savedImageUri)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch the image picker
                imagePickerLauncher.launch(intent)
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Write permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }





    fun clearHistory() {
        // Clear preferences
        MyPreferences(this@MainActivity).saveHistory(this@MainActivity, mutableListOf())
        // Clear drawer
        historyAdapter.clearHistory()
    }

    private fun keyVibration(view: View) {
        if (MyPreferences(this).vibrationMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
        }
    }

    private fun setErrorColor(errorStatus: Boolean) {
        // Only run if the color needs to be updated
        if (errorStatus != errorStatusOld) {
            // Set error color
            if (errorStatus) {
                binding.formula.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.calculation_error_color
                    )
                )
                binding.output.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.calculation_error_color
                    )
                )
            }
            // Clear error color
            else {
                binding.formula.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.output.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )
            }
            errorStatusOld = errorStatus
        }
    }

    private fun updateDisplay(view: View, value: String) {
        // Reset input with current number if following "equal"
        if (isEqualLastAction) {
            val anyNumber = "0123456789$decimalSeparatorSymbol".toCharArray().map {
                it.toString()
            }
            if (anyNumber.contains(value)) {
                binding.formula.setText("")
            } else {
                binding.formula.setSelection(binding.formula.text.length)
                binding.inputHorizontalScrollView!!.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
            }
            isEqualLastAction = false
        }

        if (!binding.formula.isCursorVisible) {
            binding.formula.isCursorVisible = true
        }

        lifecycleScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                // Vibrate when key pressed
                keyVibration(view)
            }

            val formerValue = binding.formula.text.toString()
            val cursorPosition = binding.formula.selectionStart
            val leftValue = formerValue.subSequence(0, cursorPosition).toString()
            val rightValue = formerValue.subSequence(cursorPosition, formerValue.length).toString()

            val newValue = leftValue + value + rightValue

            var newValueFormatted =
                NumberFormatter.format(newValue, decimalSeparatorSymbol, groupingSeparatorSymbol)

            withContext(Dispatchers.Main) {
                // Avoid two decimalSeparator in the same number
                // 1. When you click on the decimalSeparator button
                if (value == decimalSeparatorSymbol && decimalSeparatorSymbol in binding.formula.text.toString()) {
                    if (binding.formula.text.toString().isNotEmpty()) {
                        var lastNumberBefore = ""
                        if (cursorPosition > 0 && binding.formula.text.toString()
                                .substring(0, cursorPosition)
                                .last() in "0123456789\\$decimalSeparatorSymbol"
                        ) {
                            lastNumberBefore = NumberFormatter.extractNumbers(
                                binding.formula.text.toString().substring(0, cursorPosition),
                                decimalSeparatorSymbol
                            ).last()
                        }
                        var firstNumberAfter = ""
                        if (cursorPosition < binding.formula.text.length - 1) {
                            firstNumberAfter = NumberFormatter.extractNumbers(
                                binding.formula.text.toString()
                                    .substring(cursorPosition, binding.formula.text.length),
                                decimalSeparatorSymbol
                            ).first()
                        }
                        if (decimalSeparatorSymbol in lastNumberBefore || decimalSeparatorSymbol in firstNumberAfter) {
                            return@withContext
                        }
                    }
                }
                // 2. When you click on a former calculation from the history
                if (binding.formula.text.isNotEmpty()
                    && cursorPosition > 0
                    && decimalSeparatorSymbol in value
                    && value != decimalSeparatorSymbol // The value should not be *only* the decimal separator
                ) {
                    if (NumberFormatter.extractNumbers(value, decimalSeparatorSymbol)
                            .isNotEmpty()
                    ) {
                        val firstValueNumber =
                            NumberFormatter.extractNumbers(value, decimalSeparatorSymbol).first()
                        val lastValueNumber =
                            NumberFormatter.extractNumbers(value, decimalSeparatorSymbol).last()
                        if (decimalSeparatorSymbol in firstValueNumber || decimalSeparatorSymbol in lastValueNumber) {
                            var numberBefore =
                                binding.formula.text.toString().substring(0, cursorPosition)
                            if (numberBefore.last() !in "()*-/+^!√πe") {
                                numberBefore = NumberFormatter.extractNumbers(
                                    numberBefore,
                                    decimalSeparatorSymbol
                                ).last()
                            }
                            var numberAfter = ""
                            if (cursorPosition < binding.formula.text.length - 1) {
                                numberAfter = NumberFormatter.extractNumbers(
                                    binding.formula.text.toString()
                                        .substring(cursorPosition, binding.formula.text.length),
                                    decimalSeparatorSymbol
                                ).first()
                            }
                            var tmpValue = value
                            var numberBeforeParenthesisLength = 0
                            if (decimalSeparatorSymbol in numberBefore) {
                                numberBefore = "($numberBefore)"
                                numberBeforeParenthesisLength += 2
                            }
                            if (decimalSeparatorSymbol in numberAfter) {
                                tmpValue = "($value)"
                            }
                            val tmpNewValue = binding.formula.text.toString().substring(
                                0,
                                (cursorPosition + numberBeforeParenthesisLength - numberBefore.length)
                            ) + numberBefore + tmpValue + rightValue
                            newValueFormatted = NumberFormatter.format(
                                tmpNewValue,
                                decimalSeparatorSymbol,
                                groupingSeparatorSymbol
                            )
                        }
                    }
                }

                // Update Display
                binding.formula.setText(newValueFormatted)

                // Increase cursor position
                val cursorOffset = newValueFormatted.length - newValue.length
                binding.formula.setSelection(cursorPosition + value.length + cursorOffset)
            }
        }
    }

    private fun roundResult(result: BigDecimal): BigDecimal {
        val numberPrecision = MyPreferences(this).numberPrecision!!.toInt()
        var newResult = result.setScale(numberPrecision, RoundingMode.HALF_EVEN)
        if (MyPreferences(this).numberIntoScientificNotation && (newResult >= BigDecimal(9999) || newResult <= BigDecimal(
                0.1
            ))
        ) {
            val scientificString = String.format(Locale.US, "%.4g", result)
            newResult = BigDecimal(scientificString)
        }

        // Fix how is displayed 0 with BigDecimal
        if (
            "0E-" in newResult.toString()
            || (newResult.toString().startsWith("0.000") && newResult.toString().endsWith("000"))
        ) {
            return BigDecimal.ZERO
        }

        return newResult
    }

    private fun enableOrDisableScientistMode() {
        if (binding.scientistModeRow2.visibility != View.VISIBLE) {
            binding.scientistModeRow2.visibility = View.VISIBLE
            binding.scientistModeRow3.visibility = View.VISIBLE
            binding.scientistModeSwitchButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            binding.degreeTextView.visibility = View.VISIBLE
            binding.degreeTextView.text = binding.degreeButton.text.toString()
        } else {
            binding.scientistModeRow2.visibility = View.GONE
            binding.scientistModeRow3.visibility = View.GONE
            binding.scientistModeSwitchButton.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            binding.degreeTextView.visibility = View.GONE
            binding.degreeTextView.text = binding.degreeButton.text.toString()
        }
    }

    // Switch between degree and radian mode
    private fun toggleDegreeMode() {
        if (isDegreeModeActivated) binding.degreeButton.text = getString(R.string.radian)
        else binding.degreeButton.text = getString(R.string.degree)

        binding.degreeTextView.text = binding.degreeButton.text

        // Flip the variable afterwards
        isDegreeModeActivated = !isDegreeModeActivated
    }

    @SuppressLint("SetTextI18n")
    private fun updateResultDisplay() {
        lifecycleScope.launch(Dispatchers.Default) {
            // Reset text color
            setErrorColor(false)

            val calculation = binding.formula.text.toString()

            if (calculation != "") {
                division_by_0 = false
                domain_error = false
                syntax_error = false
                is_infinity = false

                val calculationTmp = Expression().getCleanExpression(
                    binding.formula.text.toString(),
                    decimalSeparatorSymbol,
                    groupingSeparatorSymbol
                )
                calculationResult =
                    Calculator(MyPreferences(this@MainActivity).numberPrecision!!.toInt()).evaluate(
                        calculationTmp,
                        isDegreeModeActivated
                    )

                // If result is a number and it is finite
                if (!(division_by_0 || domain_error || syntax_error || is_infinity)) {

                    // Round
                    calculationResult = roundResult(calculationResult)
                    var formattedResult = NumberFormatter.format(
                        calculationResult.toString().replace(".", decimalSeparatorSymbol),
                        decimalSeparatorSymbol,
                        groupingSeparatorSymbol
                    )

                    // Remove zeros at the end of the results (after point)
                    if (!MyPreferences(this@MainActivity).numberIntoScientificNotation || !(calculationResult >= BigDecimal(
                            9999
                        ) || calculationResult <= BigDecimal(0.1))
                    ) {
                        val resultSplited = calculationResult.toString().split('.')
                        if (resultSplited.size > 1) {
                            val resultPartAfterDecimalSeparator = resultSplited[1].trimEnd('0')
                            var resultWithoutZeros = resultSplited[0]
                            if (resultPartAfterDecimalSeparator != "") {
                                resultWithoutZeros =
                                    resultSplited[0] + "." + resultPartAfterDecimalSeparator
                            }
                            formattedResult = NumberFormatter.format(
                                resultWithoutZeros.replace(
                                    ".",
                                    decimalSeparatorSymbol
                                ), decimalSeparatorSymbol, groupingSeparatorSymbol
                            )
                        }
                    }


                    withContext(Dispatchers.Main) {
                        if (formattedResult != calculation) {
                            binding.output.text = formattedResult
                        } else {
                            binding.output.text = ""
                        }
                    }

                } else withContext(Dispatchers.Main) {
                    if (is_infinity && !division_by_0 && !domain_error) {
                        if (calculationResult < BigDecimal.ZERO) binding.output.text = "-" + getString(
                            R.string.infinity
                        )
                        else binding.output.text = getString(R.string.value_too_large)
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.output.text = ""
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.output.text = ""
                }
            }
        }
    }

    fun keyDigitPadMappingToDisplay(view: View) {
        updateDisplay(view, (view as Button).text as String)
    }

    @SuppressLint("SetTextI18n")
    private fun addSymbol(view: View, currentSymbol: String) {
        // Get input text length
        val textLength = binding.formula.text.length

        // If the input is not empty
        if (textLength > 0) {
            // Get cursor's current position
            val cursorPosition = binding.formula.selectionStart

            // Get next / previous characters relative to the cursor
            val nextChar =
                if (textLength - cursorPosition > 0) binding.formula.text[cursorPosition].toString() else "0" // use "0" as default like it's not a symbol
            val previousChar =
                if (cursorPosition > 0) binding.formula.text[cursorPosition - 1].toString() else "0"

            if (currentSymbol != previousChar // Ignore multiple presses of the same button
                && currentSymbol != nextChar
                && previousChar != "√" // No symbol can be added on an empty square root
                && previousChar != decimalSeparatorSymbol // Ensure that the previous character is not a comma
                && nextChar != decimalSeparatorSymbol // Ensure that the next character is not a comma
                && (previousChar != "(" // Ensure that we are not at the beginning of a parenthesis
                        || currentSymbol == "-")
            ) { // Minus symbol is an override
                // If previous character is a symbol, replace it
                if (previousChar.matches("[+\\-÷×^]".toRegex())) {
                    keyVibration(view)

                    val leftString =
                        binding.formula.text.subSequence(0, cursorPosition - 1).toString()
                    val rightString =
                        binding.formula.text.subSequence(cursorPosition, textLength).toString()

                    // Add a parenthesis if there is another symbol before minus
                    if (currentSymbol == "-") {
                        if (previousChar in "+-") {
                            binding.formula.setText(leftString + currentSymbol + rightString)
                            binding.formula.setSelection(cursorPosition)
                        } else {
                            binding.formula.setText(leftString + previousChar + currentSymbol + rightString)
                            binding.formula.setSelection(cursorPosition + 1)
                        }
                    } else if (cursorPosition > 1 && binding.formula.text[cursorPosition - 2] != '(') {
                        binding.formula.setText(leftString + currentSymbol + rightString)
                        binding.formula.setSelection(cursorPosition)
                    } else if (currentSymbol == "+") {
                        binding.formula.setText(leftString + rightString)
                        binding.formula.setSelection(cursorPosition - 1)
                    }
                }
                // If next character is a symbol, replace it
                else if (nextChar.matches("[+\\-÷×^%!]".toRegex())
                    && currentSymbol != "%"
                ) { // Make sure that percent symbol doesn't replace succeeding symbols
                    keyVibration(view)

                    val leftString = binding.formula.text.subSequence(0, cursorPosition).toString()
                    val rightString =
                        binding.formula.text.subSequence(cursorPosition + 1, textLength).toString()

                    if (cursorPosition > 0 && previousChar != "(") {
                        binding.formula.setText(leftString + currentSymbol + rightString)
                        binding.formula.setSelection(cursorPosition + 1)
                    } else if (currentSymbol == "+") binding.formula.setText(leftString + rightString)
                }
                // Otherwise just update the display
                else if (cursorPosition > 0 || nextChar != "0" && currentSymbol == "-") {
                    updateDisplay(view, currentSymbol)
                } else keyVibration(view)
            } else keyVibration(view)
        } else { // Allow minus symbol, even if the input is empty
            if (currentSymbol == "-") updateDisplay(view, currentSymbol)
            else keyVibration(view)
        }
    }

    fun addButton(view: View) {
        addSymbol(view, "+")
    }

    fun subtractButton(view: View) {
        addSymbol(view, "-")
    }

    fun divideButton(view: View) {
        addSymbol(view, "÷")
    }

    fun multiplyButton(view: View) {
        addSymbol(view, "×")
    }

    fun exponentButton(view: View) {
        addSymbol(view, "^")
    }

    fun pointButton(view: View) {
        updateDisplay(view, decimalSeparatorSymbol)
    }

    fun sineButton(view: View) {
        if (!isInvButtonClicked) {
            updateDisplay(view, "sin(")
        } else {
            updateDisplay(view, "sin⁻¹(")
        }
    }

    fun cosineButton(view: View) {
        if (!isInvButtonClicked) {
            updateDisplay(view, "cos(")
        } else {
            updateDisplay(view, "cos⁻¹(")
        }
    }

    fun tangentButton(view: View) {
        if (!isInvButtonClicked) {
            updateDisplay(view, "tan(")
        } else {
            updateDisplay(view, "tan⁻¹(")
        }
    }

    fun eButton(view: View) {
        updateDisplay(view, "e")
    }

    fun naturalLogarithmButton(view: View) {
        if (!isInvButtonClicked) {
            updateDisplay(view, "ln(")
        } else {
            updateDisplay(view, "exp(")
        }
    }

    fun logarithmButton(view: View) {
        if (!isInvButtonClicked) {
            updateDisplay(view, "log(")
        } else {
            updateDisplay(view, "10^")
        }
    }

    fun piButton(view: View) {
        updateDisplay(view, "π")
    }

    fun factorialButton(view: View) {
        addSymbol(view, "!")
    }

    fun squareButton(view: View) {
        if (!isInvButtonClicked) {
            updateDisplay(view, "√")
        } else {
            if (MyPreferences(this).addModuloButton) {
                updateDisplay(view, "#")
            } else {
                updateDisplay(view, "^2")
            }

        }
    }

    fun percent(view: View) {
        addSymbol(view, "%")
    }

    @SuppressLint("SetTextI18n")
    fun degreeButton(view: View) {
        keyVibration(view)
        toggleDegreeMode()
        updateResultDisplay()
    }

    fun invButton(view: View) {
        keyVibration(view)

        if (!isInvButtonClicked) {
            isInvButtonClicked = true

            // change buttons
            binding.sineButton.setText(R.string.sineInv)
            binding.cosineButton.setText(R.string.cosineInv)
            binding.tangentButton.setText(R.string.tangentInv)
            binding.naturalLogarithmButton.setText(R.string.naturalLogarithmInv)
            binding.logarithmButton.setText(R.string.logarithmInv)
            if (MyPreferences(this).addModuloButton) {
                binding.squareButton.setText(R.string.squareInvModuloVersion)
            } else {
                binding.squareButton.setText(R.string.squareInv)
            }

        } else {
            isInvButtonClicked = false

            // change buttons
            binding.sineButton.setText(R.string.sine)
            binding.cosineButton.setText(R.string.cosine)
            binding.tangentButton.setText(R.string.tangent)
            binding.naturalLogarithmButton.setText(R.string.naturalLogarithm)
            binding.logarithmButton.setText(R.string.logarithm)
            binding.squareButton.setText(R.string.square)
        }
    }

    fun clearButton(view: View) {
        keyVibration(view)
        binding.formula.setText("")
        binding.output.text = ""
    }

    @SuppressLint("SetTextI18n")
    fun equalsButton(view: View) {
        lifecycleScope.launch(Dispatchers.Default) {
            keyVibration(view)

            val calculation = binding.formula.text.toString()

            if (calculation != "") {

                val resultString = calculationResult.toString()
                var formattedResult = NumberFormatter.format(
                    resultString.replace(".", decimalSeparatorSymbol),
                    decimalSeparatorSymbol,
                    groupingSeparatorSymbol
                )

                // If result is a number and it is finite
                if (!(division_by_0 || domain_error || syntax_error || is_infinity)) {

                    // Remove zeros at the end of the results (after point)
                    val resultSplited = resultString.split('.')
                    if (resultSplited.size > 1) {
                        val resultPartAfterDecimalSeparator = resultSplited[1].trimEnd('0')
                        var resultWithoutZeros = resultSplited[0]
                        if (resultPartAfterDecimalSeparator != "") {
                            resultWithoutZeros =
                                resultSplited[0] + "." + resultPartAfterDecimalSeparator
                        }
                        formattedResult = NumberFormatter.format(
                            resultWithoutZeros.replace(
                                ".",
                                decimalSeparatorSymbol
                            ), decimalSeparatorSymbol, groupingSeparatorSymbol
                        )
                    }

                    // Hide the cursor before updating binding.input to avoid weird cursor movement
                    withContext(Dispatchers.Main) {
                        binding.formula.isCursorVisible = false
                    }

                    // Display result
                    withContext(Dispatchers.Main) { binding.formula.setText(formattedResult) }

                    // Set cursor
                    withContext(Dispatchers.Main) {
                        // Scroll to the end
                        binding.formula.setSelection(binding.formula.length())

                        // Hide the cursor (do not remove this, it's not a duplicate)
                        binding.formula.isCursorVisible = false

                        // Clear resultDisplay
                        binding.output.text = ""
                    }

                    if (calculation != formattedResult) {
                        val history = MyPreferences(this@MainActivity).getHistory()

                        // Do not save to history if the previous entry is the same as the current one
                        if (history.isEmpty() || history[history.size - 1].calculation != calculation) {
                            // Store time
                            val currentTime = System.currentTimeMillis().toString()

                            // Save to history
                            history.add(
                                History(
                                    calculation = calculation,
                                    result = formattedResult,
                                    time = currentTime,
                                )
                            )

                            MyPreferences(this@MainActivity).saveHistory(this@MainActivity, history)

                            // Update history variables
                            withContext(Dispatchers.Main) {
                                historyAdapter.appendOneHistoryElement(
                                    History(
                                        calculation = calculation,
                                        result = formattedResult,
                                        time = currentTime,
                                    )
                                )

                                // Remove former results if > historySize preference
                                val historySize =
                                    MyPreferences(this@MainActivity).historySize!!.toInt()
                                while (historySize > 0 && historyAdapter.itemCount >= historySize) {
                                    historyAdapter.removeFirstHistoryElement()
                                }

                                // Scroll to the bottom of the recycle view
                                binding.historyRecylcleView.scrollToPosition(historyAdapter.itemCount - 1)
                            }
                        }
                    }
                    isEqualLastAction = true
                } else {
                    withContext(Dispatchers.Main) {
                        if (syntax_error) {
                            setErrorColor(true)
                            binding.output.text = getString(R.string.syntax_error)
                        } else if (domain_error) {
                            setErrorColor(true)
                            binding.output.text = getString(R.string.domain_error)
                        } else if (division_by_0) {
                            setErrorColor(true)
                            binding.output.text = getString(R.string.division_by_0)
                        } else if (is_infinity) {
                            if (calculationResult < BigDecimal.ZERO) binding.output.text = "-" + getString(
                                R.string.infinity
                            )
                            else binding.output.text = getString(R.string.value_too_large)
                            //} else if (result.isNaN()) {
                            //    setErrorColor(true)
                            //    binding.resultDisplay.setText(getString(R.string.math_error))
                        } else {
                            binding.output.text = formattedResult
                            isEqualLastAction =
                                true // Do not clear the calculation (if you click into a number) if there is an error
                        }
                    }
                }

            } else {
                withContext(Dispatchers.Main) { binding.output.text = "" }
            }
        }
    }

    fun leftParenthesisButton(view: View) {
        updateDisplay(view, "(")
    }

    fun rightParenthesisButton(view: View) {
        updateDisplay(view, ")")
    }

    fun parenthesesButton(view: View) {
        val cursorPosition = binding.formula.selectionStart
        val textLength = binding.formula.text.length

        var openParentheses = 0
        var closeParentheses = 0

        val text = binding.formula.text.toString()

        for (i in 0 until cursorPosition) {
            if (text[i] == '(') {
                openParentheses += 1
            }
            if (text[i] == ')') {
                closeParentheses += 1
            }
        }

        if (
            !(textLength > cursorPosition && binding.formula.text.toString()[cursorPosition] in "×÷+-^")
            && (
                    openParentheses == closeParentheses
                            || binding.formula.text.toString()[cursorPosition - 1] == '('
                            || binding.formula.text.toString()[cursorPosition - 1] in "×÷+-^"
                    )
        ) {
            updateDisplay(view, "(")
        } else {
            updateDisplay(view, ")")
        }
    }

    fun backspaceButton(view: View) {
        keyVibration(view)

        var cursorPosition = binding.formula.selectionStart
        val textLength = binding.formula.text.length
        var newValue = ""
        var isFunction = false
        var functionLength = 0

        if (isEqualLastAction) {
            cursorPosition = textLength
        }

        if (cursorPosition != 0 && textLength != 0) {
            // Check if it is a function to delete
            val functionsList =
                listOf("cos⁻¹(", "sin⁻¹(", "tan⁻¹(", "cos(", "sin(", "tan(", "ln(", "log(", "exp(")
            for (function in functionsList) {
                val leftPart = binding.formula.text.subSequence(0, cursorPosition).toString()
                if (leftPart.endsWith(function)) {
                    newValue = binding.formula.text.subSequence(0, cursorPosition - function.length)
                        .toString() +
                            binding.formula.text.subSequence(cursorPosition, textLength).toString()
                    isFunction = true
                    functionLength = function.length - 1
                    break
                }
            }
            // Else
            if (!isFunction) {
                // remove the grouping separator
                val leftPart = binding.formula.text.subSequence(0, cursorPosition).toString()
                val leftPartWithoutSpaces = leftPart.replace(groupingSeparatorSymbol, "")
                functionLength = leftPart.length - leftPartWithoutSpaces.length

                newValue = leftPartWithoutSpaces.subSequence(0, leftPartWithoutSpaces.length - 1)
                    .toString() +
                        binding.formula.text.subSequence(cursorPosition, textLength).toString()
            }

            val newValueFormatted =
                NumberFormatter.format(newValue, decimalSeparatorSymbol, groupingSeparatorSymbol)
            var cursorOffset = newValueFormatted.length - newValue.length
            if (cursorOffset < 0) cursorOffset = 0

            binding.formula.setText(newValueFormatted)
            binding.formula.setSelection((cursorPosition - 1 + cursorOffset - functionLength).takeIf { it > 0 }
                ?: 0)
        }
    }

    fun scientistModeSwitchButton(view: View) {
        enableOrDisableScientistMode()
    }

}

//    private fun addToInputText(buttonValue: String): String{
//        return "${formula.text}$buttonValue"
//    }


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


