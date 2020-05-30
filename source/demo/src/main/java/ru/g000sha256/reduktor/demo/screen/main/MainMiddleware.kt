package ru.g000sha256.reduktor.demo.screen.main

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import ru.g000sha256.reduktor.Middleware
import ru.g000sha256.reduktor.demo.extension.startWithItem
import ru.g000sha256.reduktor.demo.model.User
import ru.g000sha256.schedulers.SchedulersHolder
import java.util.concurrent.TimeUnit

class MainMiddleware(
        private val errorProvider: MainErrorProvider,
        private val repository: MainRepository,
        private val schedulersHolder: SchedulersHolder
) : Middleware<MainAction, MainState> {

    override fun beforeReduce(
            actionObservable: Observable<MainAction>,
            stateAccessor: () -> MainState
    ): Observable<MainAction> {
        val startObservable = createStartObservable(stateAccessor)
        return actionObservable
                .flatMap {
                    when (it) {
                        is MainAction.ActivityNotFoundError -> {
                            val action = MainAction.Show.Toast(errorProvider.activityNotFoundError)
                            return@flatMap Observable.just(action)
                        }
                        is MainAction.Click.Dialog -> {
                            val action1 = MainAction.ClearDialogUserId()
                            val userId = it.userId ?: return@flatMap Observable.just(action1)
                            val state = stateAccessor()
                            var user = state.users.find { user -> user.id == userId }
                            if (user == null && state.user?.id == userId) user = state.user
                            user ?: return@flatMap Observable.just(action1)
                            val action2 = MainAction.OpenBrowser(user.browserUrl)
                            return@flatMap Observable.just(action1, action2)
                        }
                        is MainAction.Click.Item -> {
                            val action = MainAction.Show.Dialog(it.userId)
                            return@flatMap Observable.just(action)
                        }
                        is MainAction.Click.Retry -> {
                            val action1 = MainAction.ClearErrors()
                            val state = stateAccessor()
                            val isEmpty = state.users.isEmpty()
                            if (isEmpty) {
                                val action2 = MainAction.StartLoading.First()
                                return@flatMap Observable.just(action1, action2)
                            } else {
                                val action2 = MainAction.StartLoading.Next()
                                return@flatMap Observable.just(action1, action2)
                            }
                        }
                        is MainAction.Init -> {
                            val state = stateAccessor()
                            val dialogUserId = state.dialogUserId ?: return@flatMap Observable.empty<MainAction>()
                            val action = MainAction.Show.Dialog(dialogUserId)
                            return@flatMap Observable.just(action)
                        }
                        is MainAction.Load.Reload.Error -> {
                            val text = errorProvider.getNetworkError(it.throwable)
                            val action = MainAction.Show.SnackBar(text)
                            return@flatMap Observable.just(action)
                        }
                        is MainAction.StartLoading.First -> {
                            val state = stateAccessor()
                            if (state.hasNextPageLoading || state.hasReloadPageLoading) {
                                val action = MainAction.StopLoading()
                                return@flatMap Observable.just(action, it)
                            }
                            if (!state.allowLoadMore) return@flatMap Observable.empty<MainAction>()
                            if (state.hasFirstPageError) return@flatMap Observable.empty<MainAction>()
                            if (state.hasFirstPageLoading) return@flatMap Observable.empty<MainAction>()
                            val usersObservable = createLoadObservable(actionObservable, lastUserId = null)
                            val userObservable = repository
                                    .loadUser()
                                    .toObservable()
                            val biFunction = BiFunction<User, List<User>, Pair<User, List<User>>> { user, users -> user to users }
                            return@flatMap Observable
                                    .zip(userObservable, usersObservable, biFunction)
                                    .map<MainAction> { pair -> MainAction.Load.First.Data(pair.first, pair.second) }
                                    .onErrorReturn { throwable -> MainAction.Load.First.Error(throwable) }
                                    .startWithItem { MainAction.Load.First.Loading() }
                        }
                        is MainAction.StartLoading.Next -> {
                            val state = stateAccessor()
                            if (state.hasFirstPageLoading || state.hasReloadPageLoading) {
                                val action = MainAction.StopLoading()
                                return@flatMap Observable.just(action, it)
                            }
                            if (!state.allowLoadMore) return@flatMap Observable.empty<MainAction>()
                            if (state.hasNextPageError) return@flatMap Observable.empty<MainAction>()
                            if (state.hasNextPageLoading) return@flatMap Observable.empty<MainAction>()
                            val lastUser = state.users.lastOrNull()
                            return@flatMap createLoadObservable(actionObservable, lastUser?.id)
                                    .map<MainAction> { users -> MainAction.Load.Next.Data(users) }
                                    .onErrorReturn { throwable -> MainAction.Load.Next.Error(throwable) }
                                    .startWithItem { MainAction.Load.Next.Loading() }
                        }
                        is MainAction.StartLoading.Reload -> {
                            val state = stateAccessor()
                            if (state.hasFirstPageLoading || state.hasNextPageLoading) {
                                val action = MainAction.StopLoading()
                                return@flatMap Observable.just(action, it)
                            }
                            if (!state.allowLoadMore) return@flatMap Observable.empty<MainAction>()
                            if (state.hasReloadPageLoading) return@flatMap Observable.empty<MainAction>()
                            return@flatMap createLoadObservable(actionObservable, lastUserId = null)
                                    .map<MainAction> { users -> MainAction.Load.Reload.Data(users) }
                                    .onErrorReturn { throwable -> MainAction.Load.Reload.Error(throwable) }
                                    .startWithItem { MainAction.Load.Reload.Loading() }
                        }
                        else -> return@flatMap Observable.empty<MainAction>()
                    }
                }
                .mergeWith(startObservable)
    }

    override fun afterReduce(actionObservable: Observable<MainAction>, stateAccessor: () -> MainState): Observable<MainAction> {
        return Observable.empty()
    }

    private fun createLoadObservable(actionObservable: Observable<MainAction>, lastUserId: Long?): Observable<List<User>> {
        val timerSingle = Single
                .timer(250L, TimeUnit.MILLISECONDS, schedulersHolder.computationScheduler)
                .observeOn(schedulersHolder.ioScheduler)
        val stopObservable = actionObservable.ofType(MainAction.StopLoading::class.java)
        return repository
                .loadUsers(lastUserId)
                .onErrorResumeNext { throwable ->
                    return@onErrorResumeNext timerSingle
                            .doOnSuccess { throw throwable }
                            .map { emptyList<User>() }
                }
                .toObservable()
                .takeUntil(stopObservable)
    }

    private fun createStartObservable(stateAccessor: () -> MainState): Observable<MainAction> {
        val action1 = MainAction.StopLoading()
        val state = stateAccessor()
        val isEmpty = state.users.isEmpty()
        if (isEmpty) {
            val action2 = MainAction.StartLoading.First()
            return Observable.just(action1, action2)
        }
        return Observable.just(action1)
    }

}