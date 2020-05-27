package ru.g000sha256.reduktor.demo.screen.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.g000sha256.reduktor.demo.extension.plusAssign
import ru.g000sha256.schedulers_factory.SchedulersFactory

class MainRouter(
        private val context: Context,
        private val routeEventObservable: Observable<MainRouteEvent>,
        private val schedulersFactory: SchedulersFactory,
        private val actionConsumer: (MainAction) -> Unit
) {

    private val compositeDisposable = CompositeDisposable()

    fun onAttach() {
        compositeDisposable += routeEventObservable
                .observeOn(schedulersFactory.mainDeferredScheduler)
                .subscribe {
                    when (it) {
                        is MainRouteEvent.OpenBrowser -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(it.url)
                            try {
                                context.startActivity(intent)
                            } catch (throwable: Throwable) {
                                val action = MainAction.ActivityNotFoundError()
                                actionConsumer(action)
                            }
                        }
                    }
                }
    }

    fun onDetach() {
        compositeDisposable.clear()
    }

}