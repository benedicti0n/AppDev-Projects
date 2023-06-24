package com.example.mydictionary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var searchButton : ImageView
//    private lateinit var searchBar : androidx.appcompat.widget.SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchButton = findViewById(R.id.search_button)
//        searchBar = findViewById(R.id.search_bar)

        searchButton.setOnClickListener{
            val Intent = Intent(this, MainActivity2::class.java)
//                .putExtra("search_query", searchBar.tex.toString())
            startActivity(Intent)
        }

    }
}