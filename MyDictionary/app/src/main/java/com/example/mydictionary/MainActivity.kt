package com.example.mydictionary

import RequestManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydictionary.Adapters.MeaningAdapter
import com.example.mydictionary.Adapters.PhoneticsAdapter
import com.example.mydictionary.Models.APIResponse


class MainActivity : AppCompatActivity() {

    private lateinit var search_result: androidx.appcompat.widget.SearchView
    private lateinit var recycler_phonetics: RecyclerView
    private lateinit var recycler_meanings: RecyclerView
    private lateinit var phoneticsAdapter: PhoneticsAdapter
    private lateinit var meaningAdapter: MeaningAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_result = findViewById(R.id.search_result)
        recycler_phonetics = findViewById(R.id.recycler_phonetics)
        recycler_meanings = findViewById(R.id.recycler_meanings)


        val manager = RequestManager(this@MainActivity)
        manager.getWordMeaning(listener, "Hello")


//            val searchQuery: String? = intent.getStringExtra("search_query")
//            val editableSearchQuery: Editable? = searchQuery?.let { Editable.Factory.getInstance().newEditable(it) }
//            searchResult.text = editableSearchQuery

        search_result.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Handle query submission


                val manager = RequestManager(this@MainActivity)
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
                Toast.makeText(this@MainActivity, "No data found :(", Toast.LENGTH_SHORT).show()
                return
            }
            showData(apiResponse)
        }

        override fun onError(message: String) {
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
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