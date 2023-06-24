package com.example.mydictionary.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mydictionary.Models.Meanings
import com.example.mydictionary.R
import com.example.mydictionary.ViewHolders.MeaningViewHolder

class MeaningAdapter(private val context: Context?, private val meaningsList: List<Meanings>) : RecyclerView.Adapter<MeaningViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeaningViewHolder {
        return MeaningViewHolder(LayoutInflater.from(context).inflate(R.layout.meanings_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return meaningsList.size
    }

    override fun onBindViewHolder(holder: MeaningViewHolder, position: Int) {
        holder.textView_partsOfSpeech.text = meaningsList[position].partOfSpeech
        holder.recycler_definition.setHasFixedSize(true)
        holder.recycler_definition.layoutManager = GridLayoutManager(context, 1)

        val definitionAdapter = DefinitionAdapter(context, meaningsList[position].definition!!)
        holder.recycler_definition.adapter = definitionAdapter

    }

}