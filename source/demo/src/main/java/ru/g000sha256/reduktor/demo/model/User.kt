package ru.g000sha256.reduktor.demo.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        @SerializedName("id") val id: Long,
        @SerializedName("avatar_url") val avatarUrl: String,
        @SerializedName("html_url") val browserUrl: String,
        @SerializedName("login") val login: String
) : Parcelable