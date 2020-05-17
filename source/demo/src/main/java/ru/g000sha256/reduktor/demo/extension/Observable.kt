package ru.g000sha256.reduktor.demo.extension

import io.reactivex.rxjava3.core.Observable

fun <T> Observable<T>.startWithItem(callback: () -> T): Observable<T> {
    val item = callback()
    return startWithItem(item)
}