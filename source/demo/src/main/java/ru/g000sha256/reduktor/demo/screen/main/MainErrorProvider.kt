package ru.g000sha256.reduktor.demo.screen.main

import android.content.res.Resources
import ru.g000sha256.reduktor.demo.R
import ru.g000sha256.reduktor.demo.util.ApiError

class MainErrorProvider(private val resources: Resources) {

    val activityNotFoundError: String
        get() = resources.getString(R.string.error_message_activity_not_found)

    fun getNetworkError(throwable: Throwable?): String {
        if (throwable is ApiError) {
            return throwable.error
        }
        return resources.getString(R.string.error_message_network)
    }

}