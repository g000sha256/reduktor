package ru.g000sha256.reduktor.demo.screen.main

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.g000sha256.reduktor.demo.Application
import ru.g000sha256.reduktor.demo.extension.plusAssign

private const val KEY_STATE = "key_state"

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var view: MainView
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val state = savedInstanceState?.getParcelable<MainState>(KEY_STATE)
        val module = MainModule(application as Application, viewModelStore, state)
        viewModel = module.viewModel
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val actionConsumer = viewModel.actionConsumer
        val schedulersFactory = module.schedulersFactory
        view = MainView(
                actionConsumer,
                viewModel.viewEventObservable,
                viewModel.viewStateObservable,
                module.glideRequestManager,
                schedulersFactory,
                viewGroup
        )
        view.onAttach()
        compositeDisposable += viewModel
                .routeEventObservable
                .observeOn(schedulersFactory.mainDeferredScheduler)
                .subscribe {
                    when (it) {
                        is MainRouteEvent.OpenBrowser -> {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(it.url)
                            try {
                                startActivity(intent)
                            } catch (throwable: Throwable) {
                                val action = MainAction.ActivityNotFoundError()
                                actionConsumer.accept(action)
                            }
                        }
                    }
                }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_STATE, viewModel.stateAccessor.state)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        view.onDetach()
        super.onDestroy()
    }

}