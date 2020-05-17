package ru.g000sha256.reduktor.demo.screen.main

sealed class MainItem {

    data class Error(val text: String) : MainItem()

    data class Loading(private val any: Any? = null) : MainItem()

    data class User(val id: Long, val avatarUrl: String, val login: String) : MainItem()

}