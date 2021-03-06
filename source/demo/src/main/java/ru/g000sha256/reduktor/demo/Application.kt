package ru.g000sha256.reduktor.demo

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.model.GlideUrl
import com.google.gson.Gson
import okhttp3.OkHttpClient
import ru.g000sha256.scheduler.MainSchedulerFactoryImpl
import ru.g000sha256.schedulers.Schedulers
import ru.g000sha256.schedulers.SchedulersFactory
import ru.g000sha256.schedulers.SchedulersHolder
import ru.g000sha256.schedulers.SchedulersImpl
import java.io.InputStream
import java.util.concurrent.TimeUnit
import com.bumptech.glide.RequestManager as GlideRequestManager
import ru.g000sha256.reduktor.demo.network.RequestManager as ApiRequestManager

class Application : Application() {

    val apiRequestManager by lazy { ApiRequestManager(gson, okHttpClient) }
    val glideRequestManager by lazy { initGlideRequestManager(okHttpClient) }
    val schedulersFactory by lazy<SchedulersFactory> { schedulers }
    val schedulersHolder by lazy<SchedulersHolder> { schedulers }

    private val gson by lazy { Gson() }
    private val okHttpClient by lazy { initOkHttpClient() }
    private val schedulers by lazy { initSchedulers() }

    private fun initOkHttpClient(): OkHttpClient {
        val timeout = 30L
        return OkHttpClient.Builder()
                .callTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build()
    }

    private fun initGlideRequestManager(okHttpClient: OkHttpClient): GlideRequestManager {
        val memorySizeCalculator = MemorySizeCalculator.Builder(this)
                .build()
        val bitmapPoolSize = memorySizeCalculator.bitmapPoolSize.toLong()
        val bitmapPool = LruBitmapPool(bitmapPoolSize)
        val diskCacheFactory = InternalCacheDiskCacheFactory(this, "images", 128 * 1024 * 1024)
        val glideBuilder = GlideBuilder()
                .setBitmapPool(bitmapPool)
                .setDiskCache(diskCacheFactory)
                .setMemorySizeCalculator(memorySizeCalculator)
        @Suppress("VisibleForTests")
        Glide.init(this, glideBuilder)
        val glide = Glide.get(this)
        val modelLoaderFactory = OkHttpUrlLoader.Factory(okHttpClient)
        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, modelLoaderFactory)
        return glide.requestManagerRetriever.get(this)
    }

    private fun initSchedulers(): Schedulers {
        val mainSchedulerFactory = MainSchedulerFactoryImpl()
        return SchedulersImpl(mainSchedulerFactory)
    }

}