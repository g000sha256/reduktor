package ru.g000sha256.reduktor.demo.screen.main

import ru.g000sha256.reduktor.Store
import ru.g000sha256.reduktor.demo.Application
import ru.g000sha256.reduktor.demo.BuildConfig

class MainViewModelFactory(private val application: Application, private val state: MainState?) {

    fun create(): MainViewModel {
        val enableLogs = BuildConfig.DEBUG
        val errorProvider = MainErrorProvider(application.resources)
        val mapper = MainMapper(errorProvider)
        val api = MainApi(application.apiRequestManager)
        val pageLimit = MainPageLimit()
        val schedulersFactory = application.schedulersFactory
        val repository = MainRepository(api, pageLimit, schedulersFactory)
        val middleware = MainMiddleware(errorProvider, repository, schedulersFactory)
        val reducer = MainReducer(pageLimit)
        val scheduler = schedulersFactory.createOneThreadScheduler()
        val state = state ?: MainState()
        val store = Store(enableLogs, mapper, middleware, reducer, scheduler, state)
        return MainViewModel(store)
    }

}