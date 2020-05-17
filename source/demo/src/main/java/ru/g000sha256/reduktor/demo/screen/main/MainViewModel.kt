package ru.g000sha256.reduktor.demo.screen.main

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.g000sha256.reduktor.Store
import ru.g000sha256.reduktor.demo.extension.plusAssign

class MainViewModel(store: Store<MainAction, MainState, MainRouteEvent, MainViewEvent, MainViewState>) : ViewModel() {

    val actionConsumer = store.actionConsumer
    val routeEventObservable = store.routeEventObservable
    val viewEventObservable = store.viewEventObservable
    val viewStateObservable = store.viewStateObservable
    val stateAccessor = store.stateAccessor

    private val compositeDisposable = CompositeDisposable()

    init {
        compositeDisposable += store.subscribe()
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

}