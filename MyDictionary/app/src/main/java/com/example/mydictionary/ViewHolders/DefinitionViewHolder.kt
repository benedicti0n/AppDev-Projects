package com.example.mydictionary.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mydictionary.R

class DefinitionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val textView_definition : TextView = itemView.findViewById(R.id.textView_definition)
    val textView_example : TextView = itemView.findViewById(R.id.textView_example)
    val textView_synonyms : TextView = itemView.findViewById(R.id.textView_synonyms)
    val textView_antonyms : TextView = itemView.findViewById(R.id.textView_antonyms)


}