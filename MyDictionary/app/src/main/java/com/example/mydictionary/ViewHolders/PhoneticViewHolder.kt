package com.example.mydictionary.ViewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mydictionary.R


class PhoneticViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val textView_word : TextView = itemView.findViewById(R.id.textView_word)
    val textView_phonetics: TextView = itemView.findViewById(R.id.textView_phonetics)
    val imageButton_audio: ImageView = itemView.findViewById(R.id.imageButton_audio)

    }
