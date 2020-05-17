package ru.g000sha256.reduktor.demo.model

import com.google.gson.annotations.SerializedName

class Error(@SerializedName("message") val message: String)