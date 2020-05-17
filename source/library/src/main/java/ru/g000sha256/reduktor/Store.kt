package ru.g000sha256.reduktor

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.LinkedList

private const val ARROW_IN = "-->: "
private const val ARROW_OUT = "<--: "
private const val KEY_ACTION = "Action     "
private const val KEY_ROUTE_EVENT = "RouteEvent "
private const val KEY_STATE = "State      "
private const val KEY_VIEW_EVENT = "ViewEvent  "
private const val KEY_VIEW_STATE = "ViewState  "

class Store<Action, State, RouteEvent, ViewEvent, ViewState>(
        private val enableLogs: Boolean,
        private val saveEvents: Boolean,
        private val mapper: Mapper<Action, State, RouteEvent, ViewEvent, ViewState>,
        private val middleware: Middleware<Action, State>,
        private val reducer: Reducer<Action, State>,
        private val scheduler: Scheduler,
        state: State
) {

    val actionConsumer: Consumer<Action>
    val routeEventObservable: Observable<RouteEvent>
    val viewEventObservable: Observable<ViewEvent>
    val viewStateObservable: Observable<ViewState>
    val stateAccessor: StateAccessor<State>

    private val stateBehaviorSubject = BehaviorSubject.createDefault(state)
    private val routeEventLinkedList = LinkedList<RouteEvent>()
    private val viewEventLinkedList = LinkedList<ViewEvent>()
    private val actionPublishSubject = PublishSubject.create<Action>()
    private val routeEventPublishSubject = PublishSubject.create<RouteEvent>()
    private val viewEventPublishSubject = PublishSubject.create<ViewEvent>()

    private var disposable: Disposable? = null

    init {
        actionConsumer = Consumer(actionPublishSubject::onNext)
        routeEventObservable = routeEventPublishSubject
                .startWithIterable(routeEventLinkedList)
                .observeOn(scheduler)
                .doOnNext { routeEventLinkedList.remove(it) }
        viewEventObservable = viewEventPublishSubject
                .startWithIterable(viewEventLinkedList)
                .observeOn(scheduler)
                .doOnNext { viewEventLinkedList.remove(it) }
        viewStateObservable = stateBehaviorSubject
                .map(::mapViewState)
                .distinctUntilChanged()
        stateAccessor = StateAccessor<State>(stateBehaviorSubject)
    }

    fun subscribe(): Disposable {
        return disposable ?: Observable
                .just(Unit)
                .observeOn(scheduler)
                .flatMap {
                    val actionObservable = actionPublishSubject
                            .observeOn(scheduler)
                            .doOnNext { log("$KEY_ACTION$ARROW_IN$it") }
                    return@flatMap middleware.create(actionObservable, stateAccessor)
                }
                .observeOn(scheduler)
                .map {
                    log("$KEY_ACTION$ARROW_OUT$it")
                    reduceState(it)
                    mapRouteEvent(it)
                    mapViewEvent(it)
                    actionPublishSubject.onNext(it)
                }
                .doOnDispose { disposable = null }
                .doOnSubscribe { disposable = it }
                .subscribe()
    }

    private fun log(message: String) {
        if (!enableLogs) return
        println("Reduktor: $message")
    }

    private fun mapRouteEvent(action: Action) {
        log("$KEY_ROUTE_EVENT$ARROW_IN$action")
        val routeEvent = mapper.actionToRouteEvent(action) ?: return
        log("$KEY_ROUTE_EVENT$ARROW_OUT$routeEvent")
        if (saveEvents) {
            routeEventLinkedList.add(routeEvent)
        }
        routeEventPublishSubject.onNext(routeEvent)
    }

    private fun mapViewEvent(action: Action) {
        log("$KEY_VIEW_EVENT$ARROW_IN$action")
        val viewEvent = mapper.actionToViewEvent(action) ?: return
        log("$KEY_VIEW_EVENT$ARROW_OUT$viewEvent")
        if (saveEvents) {
            viewEventLinkedList.add(viewEvent)
        }
        viewEventPublishSubject.onNext(viewEvent)
    }

    private fun mapViewState(state: State): ViewState {
        log("$KEY_VIEW_STATE$ARROW_IN$state")
        val viewState = mapper.stateToViewState(state)
        log("$KEY_VIEW_STATE$ARROW_OUT$viewState")
        return viewState
    }

    private fun reduceState(action: Action) {
        val oldState = stateAccessor.state
        log("$KEY_STATE$ARROW_IN$action")
        log("$KEY_STATE$ARROW_IN$oldState")
        val newState = reducer.reduce(action, oldState)
        if (newState == oldState) return
        log("$KEY_STATE$ARROW_OUT$newState")
        stateBehaviorSubject.onNext(newState)
    }

}