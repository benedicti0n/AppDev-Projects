package com.example.mydictionary.Models

data class Meanings(
    val definition: List<Definitions>? = null,
    val partOfSpeech: String = ""
)