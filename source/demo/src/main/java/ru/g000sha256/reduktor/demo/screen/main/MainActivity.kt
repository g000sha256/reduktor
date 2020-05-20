package ru.g000sha256.reduktor.demo.screen.main

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import ru.g000sha256.reduktor.demo.Application

private const val KEY_STATE = "key_state"

class MainActivity : Activity() {

    private lateinit var router: MainRouter
    private lateinit var view: MainView
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = application as Application
        viewModel = lastNonConfigurationInstance ?: createViewModel(application, savedInstanceState)
        val schedulersFactory = application.schedulersFactory
        val actionConsumer = viewModel.actionConsumer
        router = MainRouter(this, viewModel.routeEventObservable, schedulersFactory, actionConsumer)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        view = MainView(
                viewModel.viewEventObservable,
                viewModel.viewStateObservable,
                application.glideRequestManager,
                schedulersFactory,
                actionConsumer,
                viewGroup
        )
        view.onAttach()
    }

    override fun getLastNonConfigurationInstance(): MainViewModel? {
        return super.getLastNonConfigurationInstance() as MainViewModel?
    }

    override fun onStart() {
        super.onStart()
        router.onAttach()
        viewModel.start()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

    override fun onStop() {
        router.onDetach()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val state = viewModel.stateAccessor()
        outState.putParcelable(KEY_STATE, state)
    }

    override fun onRetainNonConfigurationInstance(): MainViewModel {
        return viewModel
    }

    override fun onDestroy() {
        view.onDetach()
        if (isFinishing) viewModel.stop()
        super.onDestroy()
    }

    private fun createViewModel(application: Application, bundle: Bundle?): MainViewModel {
        val state = bundle?.getParcelable<MainState>(KEY_STATE)
        val viewModelFactory = MainViewModelFactory(application, state)
        return viewModelFactory.create()
    }

}