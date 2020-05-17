package ru.g000sha256.reduktor.demo.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import io.reactivex.rxjava3.core.Scheduler
import ru.g000sha256.reduktor.Store
import ru.g000sha256.reduktor.demo.Application

class MainModule(
        private val application: Application,
        private val viewModelStore: ViewModelStore,
        private val state: MainState?
) {

    val glideRequestManager by lazy { application.glideRequestManager }
    val schedulersFactory by lazy { application.schedulersFactory }
    val viewModel by lazy { createViewModelIfNeeded() }

    private fun createApi(): MainApi {
        return MainApi(application.apiRequestManager)
    }

    private fun createErrorProvider(): MainErrorProvider {
        return MainErrorProvider(application.resources)
    }

    private fun createMapper(errorProvider: MainErrorProvider): MainMapper {
        return MainMapper(errorProvider)
    }

    private fun createMiddleware(errorProvider: MainErrorProvider, pageLimit: MainPageLimit): MainMiddleware {
        val repository = createRepository(pageLimit)
        return MainMiddleware(errorProvider, repository, schedulersFactory)
    }

    private fun createPageLimit(): MainPageLimit {
        return MainPageLimit()
    }

    private fun createReducer(pageLimit: MainPageLimit): MainReducer {
        return MainReducer(pageLimit)
    }

    private fun createRepository(pageLimit: MainPageLimit): MainRepository {
        val api = createApi()
        return MainRepository(api, pageLimit, schedulersFactory)
    }

    private fun createScheduler(): Scheduler {
        return schedulersFactory.createOneThreadScheduler()
    }

    private fun createState(): MainState {
        return state ?: MainState()
    }

    private fun createStore(): Store<MainAction, MainState, MainRouteEvent, MainViewEvent, MainViewState> {
        val enableLogs = true
        val saveEvents = true
        val errorProvider = createErrorProvider()
        val mapper = createMapper(errorProvider)
        val pageLimit = createPageLimit()
        val middleware = createMiddleware(errorProvider, pageLimit)
        val reducer = createReducer(pageLimit)
        val scheduler = createScheduler()
        val state = createState()
        return Store(enableLogs, saveEvents, mapper, middleware, reducer, scheduler, state)
    }

    private fun createViewModel(): MainViewModel {
        val store = createStore()
        return MainViewModel(store)
    }

    private fun createViewModelIfNeeded(): MainViewModel {
        val viewModelProvider = createViewModelProvider()
        return viewModelProvider.get(MainViewModel::class.java)
    }

    private fun createViewModelProvider(): ViewModelProvider {
        val viewModelProviderFactory = createViewModelProviderFactory()
        return ViewModelProvider(viewModelStore, viewModelProviderFactory)
    }

    private fun createViewModelProviderFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {

            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass == MainViewModel::class.java) {
                    val viewModel = createViewModel()
                    return viewModel as T
                }
                throw IllegalArgumentException()
            }

        }
    }

}