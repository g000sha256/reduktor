package ru.g000sha256.reduktor.demo.screen.main

import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.core.Single
import okhttp3.Request
import ru.g000sha256.reduktor.demo.model.User
import ru.g000sha256.reduktor.demo.network.RequestManager

class MainApi(private val requestManager: RequestManager) {

    private val apiUrl = "https://api.github.com"

    fun getUser(): Single<User> {
        val request = Request.Builder()
                .get()
                .url("$apiUrl/user/46529561")
                .build()
        val typeToken = object : TypeToken<User>() {}
        return requestManager.execute(request, typeToken)
    }

    fun getUsers(limit: Int, lastUserId: Long?): Single<List<User>> {
        val stringBuilder = StringBuilder("$apiUrl/users?per_page=$limit")
        if (lastUserId != null) {
            stringBuilder.append("&since=$lastUserId")
        }
        val url = stringBuilder.toString()
        val request = Request.Builder()
                .get()
                .url(url)
                .build()
        val typeToken = object : TypeToken<List<User>>() {}
        return requestManager.execute(request, typeToken)
    }

}