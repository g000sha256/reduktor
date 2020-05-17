package ru.g000sha256.reduktor.demo.screen.main

sealed class MainViewEvent {

    sealed class Show : MainViewEvent() {

        class Dialog(val userId: Long) : Show()

        class SnackBar(val text: String) : Show()

        class Toast(val text: String) : Show()

    }

}