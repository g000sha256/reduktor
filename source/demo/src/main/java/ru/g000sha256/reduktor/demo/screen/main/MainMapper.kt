package ru.g000sha256.reduktor.demo.screen.main

import ru.g000sha256.reduktor.Mapper

class MainMapper(
        private val errorProvider: MainErrorProvider
) : Mapper<MainAction, MainState, MainRouteEvent, MainViewEvent, MainViewState> {

    override fun actionToRouteEvent(action: MainAction): MainRouteEvent? {
        when (action) {
            is MainAction.OpenBrowser -> return MainRouteEvent.OpenBrowser(action.url)
            else -> return null
        }
    }

    override fun actionToViewEvent(action: MainAction): MainViewEvent? {
        when (action) {
            is MainAction.Show.Dialog -> return MainViewEvent.Show.Dialog(action.userId)
            is MainAction.Show.SnackBar -> return MainViewEvent.Show.SnackBar(action.text)
            is MainAction.Show.Toast -> return MainViewEvent.Show.Toast(action.text)
            else -> return null
        }
    }

    override fun stateToViewState(state: MainState): MainViewState {
        when {
            state.hasFirstPageError -> {
                val text = errorProvider.getNetworkError(state.firstPageThrowable)
                return MainViewState.Error(text)
            }
            state.hasFirstPageLoading -> return MainViewState.Loading()
            state.users.isEmpty() -> return MainViewState.Loading()
            else -> {
                val items = state
                        .users
                        .map { MainItem.User(it.id, it.avatarUrl, it.login) }
                        .toMutableList<MainItem>()
                if (state.hasNextPageError) {
                    val text = errorProvider.getNetworkError(state.nextPageThrowable)
                    val item = MainItem.Error(text)
                    items.add(item)
                } else if (state.allowLoadMore) {
                    val item = MainItem.Loading()
                    items.add(item)
                }
                return MainViewState.Data(state.hasReloadPageLoading, items)
            }
        }
    }

}