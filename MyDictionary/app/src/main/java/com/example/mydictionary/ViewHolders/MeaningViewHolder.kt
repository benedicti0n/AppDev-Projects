package com.example.mydictionary.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mydictionary.R

class MeaningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val textView_partsOfSpeech : TextView = itemView.findViewById(R.id.textview_partsOfSpeech)
    val recycler_definition : RecyclerView = itemView.findViewById(R.id.recycler_definitions)

}