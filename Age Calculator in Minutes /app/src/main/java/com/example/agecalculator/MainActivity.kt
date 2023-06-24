package com.example.agecalculator

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var button : Button
    private lateinit var calender : Calendar

    private var date : TextView? = null
    private var age : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        calender = Calendar.getInstance()

        date = findViewById(R.id.date)
        age = findViewById(R.id.age)

        button.setOnClickListener{
            clickButton()
        }

    }

    fun clickButton(){

        val datePicker = DatePickerDialog(
            this, {_, selectedYear, selectedMonth, selectedDayofMonth ->
                val selectedDate = "$selectedDayofMonth/${selectedMonth+1}/$selectedYear"
                Toast.makeText(this, "Selected Date: $selectedDate", Toast.LENGTH_SHORT).show()

                date?.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                val theDate = sdf.parse(selectedDate)
                theDate?.let {
                    val selectedDateInMinutes = theDate.time/60000

                    val currentDate = sdf.parse(sdf.format(System.currentTimeMillis()))
                    currentDate?.let {
                        val currentDateInMinutes = currentDate.time/60000

                        val ageInMinutes = currentDateInMinutes - selectedDateInMinutes

                        age?.text = ageInMinutes.toString()
                    }
                }
            },
            calender.get(Calendar.YEAR),
            calender.get(Calendar.MONTH),
            calender.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()



    }

}