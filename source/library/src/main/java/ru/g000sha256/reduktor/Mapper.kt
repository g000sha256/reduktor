package ru.g000sha256.reduktor

interface Mapper<Action, State, RouteEvent, ViewEvent, ViewState> {

    fun actionToRouteEvent(action: Action): RouteEvent?

    fun actionToViewEvent(action: Action): ViewEvent?

    fun stateToViewState(state: State): ViewState

}