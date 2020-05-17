package ru.g000sha256.reduktor.demo.screen.main

import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.core.Single
import okhttp3.Request
import ru.g000sha256.reduktor.demo.model.User
import ru.g000sha256.reduktor.demo.util.RequestManager

class MainApi(private val requestManager: RequestManager) {

    fun loadUsers(limit: Int, lastUserId: Long?): Single<List<User>> {
        val stringBuilder = StringBuilder("https://api.github.com/users?per_page=$limit")
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