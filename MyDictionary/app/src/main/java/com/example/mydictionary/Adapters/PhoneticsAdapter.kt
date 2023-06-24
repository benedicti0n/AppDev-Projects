package com.example.mydictionary.Adapters

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.example.mydictionary.Models.Phonetics
import com.example.mydictionary.R
import com.example.mydictionary.ViewHolders.PhoneticViewHolder


class PhoneticsAdapter(private val context: Context?, private val phoneticsList: List<Phonetics>) : RecyclerView.Adapter<PhoneticViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneticViewHolder {
        return PhoneticViewHolder(LayoutInflater.from(context).inflate(R.layout.phonetic_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return phoneticsList.size
    }

    override fun onBindViewHolder(holder: PhoneticViewHolder, position: Int) {
        holder.textView_word.text = phoneticsList[position].text
        holder.textView_phonetics.text = phoneticsList[position].text
        holder.imageButton_audio.setOnClickListener {
            val player = MediaPlayer()
            try {
                player.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                player.setDataSource("https:" + phoneticsList[position].audio)
                player.prepare()
                player.start()

            }catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Couldn't play audio!", Toast.LENGTH_SHORT).show()
            }
    }
}


}
