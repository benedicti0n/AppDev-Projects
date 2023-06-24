package com.example.mydictionary

import RequestManager
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydictionary.Adapters.MeaningAdapter
import com.example.mydictionary.Adapters.PhoneticsAdapter
import com.example.mydictionary.Models.APIResponse
import com.example.mydictionary.R.*

class MainActivity2 : AppCompatActivity() {

    private lateinit var search_result: androidx.appcompat.widget.SearchView
    private lateinit var recycler_phonetics: RecyclerView
    private lateinit var recycler_meanings: RecyclerView
    private lateinit var phoneticsAdapter: PhoneticsAdapter
    private lateinit var meaningAdapter: MeaningAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main2)

        search_result = findViewById(id.search_result)
        recycler_phonetics = findViewById(id.recycler_phonetics)
        recycler_meanings = findViewById(id.recycler_meanings)

        val manager = RequestManager(this@MainActivity2)
        manager.getWordMeaning(listener, "Hello")


//            val searchQuery: String? = intent.getStringExtra("search_query")
//            val editableSearchQuery: Editable? = searchQuery?.let { Editable.Factory.getInstance().newEditable(it) }
//            searchResult.text = editableSearchQuery

        search_result.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Handle query submission


                val manager = RequestManager(this@MainActivity2)
                manager.getWordMeaning(listener, query)

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Handle query text change
                return false
            }
        })

    }

    private val listener = object : OnFetchDataListener {

        override fun onFetchData(apiResponse: APIResponse, message: String) {
            if (apiResponse == null) {
                Toast.makeText(this@MainActivity2, "No data found :(", Toast.LENGTH_SHORT).show()
                return
            }
            showData(apiResponse)
        }

        override fun onError(message: String) {
            Toast.makeText(this@MainActivity2, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showData(apiResponse: APIResponse) {
        recycler_phonetics.setHasFixedSize(true)
        recycler_phonetics.layoutManager = GridLayoutManager(this, 1)
        phoneticsAdapter = PhoneticsAdapter(this, apiResponse.phonetics!!)
        recycler_phonetics.adapter = phoneticsAdapter

        recycler_meanings.setHasFixedSize(true)
        recycler_meanings.layoutManager = GridLayoutManager(this, 1)
        meaningAdapter = MeaningAdapter(this, apiResponse.meanings!!)
        recycler_meanings.adapter = meaningAdapter
    }


}

