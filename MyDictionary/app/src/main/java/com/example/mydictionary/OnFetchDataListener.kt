package com.example.mydictionary

import com.example.mydictionary.Models.APIResponse

interface OnFetchDataListener {
    fun onFetchData(apiResponse: APIResponse, message: String)
    fun onError(message: String)
}
