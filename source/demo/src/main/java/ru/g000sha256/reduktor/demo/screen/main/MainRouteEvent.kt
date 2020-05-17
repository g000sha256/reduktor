package ru.g000sha256.reduktor.demo.screen.main

sealed class MainRouteEvent {

    class OpenBrowser(val url: String) : MainRouteEvent()

}