package ru.g000sha256.reduktor

import io.reactivex.rxjava3.core.Observable

interface Middleware<Action, State> {

    fun create(actionObservable: Observable<Action>, stateAccessor: StateAccessor<State>): Observable<Action>

}