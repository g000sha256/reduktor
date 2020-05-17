package ru.g000sha256.reduktor.demo.screen.main

import ru.g000sha256.reduktor.Reducer
import ru.g000sha256.reduktor.demo.model.User

class MainReducer(private val pageLimit: MainPageLimit) : Reducer<MainAction, MainState> {

    override fun reduce(action: MainAction, state: MainState): MainState {
        when (action) {
            is MainAction.ClearDialogUserId -> return state.copy(dialogUserId = null)
            is MainAction.ClearErrors -> return state.copy(firstPageThrowable = null, nextPageThrowable = null)
            is MainAction.Load.First.Data -> {
                val allowLoadMore = allowLoadMore(action.users)
                val users = ArrayList(action.users)
                return state.copy(
                        allowLoadMore = allowLoadMore,
                        hasFirstPageLoading = false,
                        hasNextPageLoading = false,
                        hasReloadPageLoading = false,
                        users = users,
                        firstPageThrowable = null,
                        nextPageThrowable = null
                )
            }
            is MainAction.Load.First.Error -> return state.copy(
                    hasFirstPageLoading = false,
                    hasNextPageLoading = false,
                    hasReloadPageLoading = false,
                    firstPageThrowable = action.throwable,
                    nextPageThrowable = null
            )
            is MainAction.Load.First.Loading -> return state.copy(
                    hasFirstPageLoading = true,
                    hasNextPageLoading = false,
                    hasReloadPageLoading = false,
                    firstPageThrowable = null,
                    nextPageThrowable = null
            )
            is MainAction.Load.Next.Data -> {
                val allowLoadMore = allowLoadMore(action.users)
                val users = ArrayList(state.users)
                users.addAll(action.users)
                return state.copy(
                        allowLoadMore = allowLoadMore,
                        hasFirstPageLoading = false,
                        hasNextPageLoading = false,
                        hasReloadPageLoading = false,
                        users = users,
                        firstPageThrowable = null,
                        nextPageThrowable = null
                )
            }
            is MainAction.Load.Next.Error -> return state.copy(
                    hasFirstPageLoading = false,
                    hasNextPageLoading = false,
                    hasReloadPageLoading = false,
                    firstPageThrowable = null,
                    nextPageThrowable = action.throwable
            )
            is MainAction.Load.Next.Loading -> return state.copy(
                    hasFirstPageLoading = false,
                    hasNextPageLoading = true,
                    hasReloadPageLoading = false,
                    firstPageThrowable = null,
                    nextPageThrowable = null
            )
            is MainAction.Load.Reload.Data -> {
                val allowLoadMore = allowLoadMore(action.users)
                val users = ArrayList(action.users)
                return state.copy(
                        allowLoadMore = allowLoadMore,
                        hasFirstPageLoading = false,
                        hasNextPageLoading = false,
                        hasReloadPageLoading = false,
                        users = users,
                        firstPageThrowable = null,
                        nextPageThrowable = null
                )
            }
            is MainAction.Load.Reload.Error -> return state.copy(
                    hasFirstPageLoading = false,
                    hasNextPageLoading = false,
                    hasReloadPageLoading = false,
                    firstPageThrowable = null,
                    nextPageThrowable = null
            )
            is MainAction.Load.Reload.Loading -> return state.copy(
                    hasFirstPageLoading = false,
                    hasNextPageLoading = false,
                    hasReloadPageLoading = true,
                    firstPageThrowable = null,
                    nextPageThrowable = null
            )
            is MainAction.Show.Dialog -> return state.copy(dialogUserId = action.userId)
            is MainAction.StopLoading -> return state.copy(
                    hasFirstPageLoading = false,
                    hasNextPageLoading = false,
                    hasReloadPageLoading = false
            )
            else -> return state
        }
    }

    private fun allowLoadMore(users: List<User>): Boolean {
        return users.size == pageLimit.value
    }

}