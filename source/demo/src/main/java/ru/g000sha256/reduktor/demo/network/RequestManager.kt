package ru.g000sha256.reduktor.demo.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.g000sha256.reduktor.demo.model.Error
import java.io.InputStreamReader
import java.io.InterruptedIOException

class RequestManager(private val gson: Gson, private val okHttpClient: OkHttpClient) {

    fun <T> execute(request: Request, typeToken: TypeToken<T>): Single<T> {
        return Single.create {
            var isCancelled = false
            it.setCancellable { isCancelled = true }
            if (isCancelled && it.isDisposed) return@create
            try {
                val call = okHttpClient.newCall(request)
                if (isCancelled && it.isDisposed) return@create
                val response = call.execute()
                if (isCancelled && it.isDisposed) return@create
                val responseBody = response.body!!
                val inputStream = responseBody.byteStream()
                val inputStreamReader = InputStreamReader(inputStream)
                if (response.isSuccessful) {
                    val data = gson.fromJson<T>(inputStreamReader, typeToken.type)
                    if (isCancelled && it.isDisposed) return@create
                    it.onSuccess(data)
                } else {
                    val error = gson.fromJson(inputStreamReader, Error::class.java)
                    val apiError = ApiError(error.message)
                    if (isCancelled && it.isDisposed) return@create
                    it.onError(apiError)
                }
            } catch (interruptedIOException: InterruptedIOException) {
            } catch (throwable: Throwable) {
                if (isCancelled && it.isDisposed) return@create
                it.onError(throwable)
            }
        }
    }

}