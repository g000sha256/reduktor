package ru.g000sha256.reduktor.demo.screen.main

import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.g000sha256.reduktor.Store
import ru.g000sha256.reduktor.demo.extension.plusAssign

class MainViewModel(private val store: Store<MainAction, MainState, MainRouteEvent, MainViewEvent, MainViewState>) {

    val actionConsumer = store.actionConsumer
    val routeEventObservable = store.routeEventObservable
    val stateAccessor = store.stateAccessor
    val viewEventObservable = store.viewEventObservable
    val viewStateObservable = store.viewStateObservable

    private val compositeDisposable = CompositeDisposable()

    fun start() {
        compositeDisposable += store.subscribe()
        val action = MainAction.Init()
        actionConsumer(action)
    }

    fun stop() {
        compositeDisposable.clear()
    }

}