package ru.g000sha256.reduktor.demo.screen.main

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.g000sha256.reduktor.demo.model.User

@Parcelize
data class MainState(
        val allowLoadMore: Boolean = true,
        val hasFirstPageLoading: Boolean = false,
        val hasNextPageLoading: Boolean = false,
        val hasReloadPageLoading: Boolean = false,
        val users: List<User> = emptyList(),
        val dialogUserId: Long? = null,
        val firstPageThrowable: Throwable? = null,
        val nextPageThrowable: Throwable? = null
) : Parcelable {

    val hasFirstPageError: Boolean
        get() = firstPageThrowable != null

    val hasNextPageError: Boolean
        get() = nextPageThrowable != null

}