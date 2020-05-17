package ru.g000sha256.reduktor.demo.screen.main

sealed class MainViewState {

    data class Data(val isRefreshing: Boolean, val items: List<MainItem>) : MainViewState()

    data class Error(val text: String) : MainViewState()

    data class Loading(private val any: Any? = null) : MainViewState()

}