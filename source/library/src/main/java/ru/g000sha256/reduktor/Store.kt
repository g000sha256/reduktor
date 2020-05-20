package ru.g000sha256.reduktor

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

private const val ARROW_IN = "--> "
private const val ARROW_OUT = "<-- "
private const val KEY_ACTION_AFTER = "Action (After)  "
private const val KEY_ACTION_BEFORE = "Action (Before) "
private const val KEY_ROUTE_EVENT = "RouteEvent      "
private const val KEY_STATE = "State           "
private const val KEY_VIEW_EVENT = "ViewEvent       "
private const val KEY_VIEW_STATE = "ViewState       "
private const val TAG = "Reduktor:"

class Store<Action, State, RouteEvent, ViewEvent, ViewState>(
        private val enableLogs: Boolean,
        private val mapper: Mapper<Action, State, RouteEvent, ViewEvent, ViewState>,
        private val middleware: Middleware<Action, State>,
        private val reducer: Reducer<Action, State>,
        private val scheduler: Scheduler,
        state: State
) {

    val routeEventObservable: Observable<RouteEvent>
    val viewEventObservable: Observable<ViewEvent>
    val viewStateObservable: Observable<ViewState>
    val actionConsumer: (Action) -> Unit = { actionPublishSubject.onNext(it) }
    val stateAccessor: () -> State = { stateBehaviorSubject.value }

    private val stateBehaviorSubject = BehaviorSubject.createDefault(state)
    private val actionPublishSubject = PublishSubject.create<Action>()
    private val routeEventPublishSubject = PublishSubject.create<RouteEvent>()
    private val viewEventPublishSubject = PublishSubject.create<ViewEvent>()

    @Volatile
    private var disposable: Disposable? = null

    init {
        routeEventObservable = routeEventPublishSubject
        viewEventObservable = viewEventPublishSubject
        viewStateObservable = stateBehaviorSubject.map(::mapViewState)
    }

    fun subscribe(): Disposable {
        return disposable ?: Observable
                .just(Unit)
                .observeOn(scheduler)
                .flatMap {
                    val afterPublishSubject = PublishSubject.create<Action>()
                    val afterActionObservable = middleware
                            .afterReduce(afterPublishSubject, stateAccessor)
                            .observeOn(scheduler)
                            .map {
                                log("$KEY_ACTION_AFTER$ARROW_OUT$it")
                                actionPublishSubject.onNext(it)
                            }
                    val beforePublishSubject = PublishSubject.create<Action>()
                    val beforeActionObservable = middleware
                            .beforeReduce(beforePublishSubject, stateAccessor)
                            .observeOn(scheduler)
                            .map {
                                log("$KEY_ACTION_BEFORE$ARROW_OUT$it")
                                actionPublishSubject.onNext(it)
                            }
                    return@flatMap actionPublishSubject
                            .observeOn(scheduler)
                            .map {
                                println(TAG)
                                log("$KEY_ACTION_BEFORE$ARROW_IN$it")
                                beforePublishSubject.onNext(it)
                                reduceState(it)
                                log("$KEY_ACTION_AFTER$ARROW_IN$it")
                                afterPublishSubject.onNext(it)
                                mapRouteEvent(it)
                                mapViewEvent(it)
                                println(TAG)
                            }
                            .mergeWith(afterActionObservable)
                            .mergeWith(beforeActionObservable)
                }
                .doOnTerminate { disposable = null }
                .subscribe()
                .apply { disposable = this }
    }

    private fun log(message: String) {
        if (!enableLogs) return
        println("$TAG $message")
        val thread = Thread.currentThread()
        println("$TAG                     ${thread.name}")
    }

    private fun mapRouteEvent(action: Action) {
        val routeEvent = mapper.actionToRouteEvent(action) ?: return
        log("$KEY_ROUTE_EVENT$ARROW_OUT$routeEvent")
        routeEventPublishSubject.onNext(routeEvent)
    }

    private fun mapViewEvent(action: Action) {
        val viewEvent = mapper.actionToViewEvent(action) ?: return
        log("$KEY_VIEW_EVENT$ARROW_OUT$viewEvent")
        viewEventPublishSubject.onNext(viewEvent)
    }

    private fun mapViewState(state: State): ViewState {
        log("$KEY_VIEW_STATE$ARROW_IN$state")
        val viewState = mapper.stateToViewState(state)
        log("$KEY_VIEW_STATE$ARROW_OUT$viewState")
        return viewState
    }

    private fun reduceState(action: Action) {
        val oldState = stateAccessor()
        log("$KEY_STATE$ARROW_IN$oldState")
        val newState = reducer.reduce(action, oldState)
        if (newState == oldState) return
        log("$KEY_STATE$ARROW_OUT$newState")
        stateBehaviorSubject.onNext(newState)
    }

}