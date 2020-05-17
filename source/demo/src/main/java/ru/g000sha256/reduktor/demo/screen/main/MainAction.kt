package ru.g000sha256.reduktor.demo.screen.main

import ru.g000sha256.reduktor.demo.model.User

sealed class MainAction {

    class ActivityNotFoundError : MainAction()

    class ClearDialogUserId : MainAction()

    class ClearErrors : MainAction()

    class OpenBrowser(val url: String) : MainAction()

    class StopLoading : MainAction()

    class ViewAttached : MainAction()

    sealed class Click : MainAction() {

        class Dialog(val userId: Long?) : Click()

        class Item(val userId: Long) : Click()

        class Retry : Click()

    }

    sealed class Load : MainAction() {

        sealed class First : Load() {

            class Data(val users: List<User>) : First()

            class Error(val throwable: Throwable) : First()

            class Loading : First()

        }

        sealed class Next : Load() {

            class Data(val users: List<User>) : Next()

            class Error(val throwable: Throwable) : Next()

            class Loading : Next()

        }

        sealed class Reload : Load() {

            class Data(val users: List<User>) : Reload()

            class Error(val throwable: Throwable) : Reload()

            class Loading : Reload()

        }

    }

    sealed class Show : MainAction() {

        class Dialog(val userId: Long) : Show()

        class SnackBar(val text: String) : Show()

        class Toast(val text: String) : Show()

    }

    sealed class StartLoading : MainAction() {

        class First : StartLoading()

        class Next : StartLoading()

        class Reload : StartLoading()

    }

}