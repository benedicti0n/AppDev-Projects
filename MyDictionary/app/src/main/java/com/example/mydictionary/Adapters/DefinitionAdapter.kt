package com.example.mydictionary.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mydictionary.Models.Definitions
import com.example.mydictionary.R
import com.example.mydictionary.ViewHolders.DefinitionViewHolder

class DefinitionAdapter (private val context: Context?, private val definitionList: List<Definitions>) : RecyclerView.Adapter<DefinitionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionViewHolder {
        return DefinitionViewHolder(LayoutInflater.from(context).inflate(R.layout.defination_list_items, parent, false))
    }

    override fun getItemCount(): Int {
        return definitionList.size
    }

    override fun onBindViewHolder(holder: DefinitionViewHolder, position: Int) {
        holder.textView_definition.text = definitionList[position].definition
        holder.textView_example.text = definitionList[position].example
        val synonyms = StringBuilder()
        val antonyms = StringBuilder()

        synonyms.append(definitionList[position].synonyms)
        antonyms.append(definitionList[position].antonyms)

        holder.textView_synonyms.text = synonyms
        holder.textView_antonyms.text = antonyms

        holder.textView_synonyms.isSelected = true
        holder.textView_antonyms.isSelected = true
    }
}