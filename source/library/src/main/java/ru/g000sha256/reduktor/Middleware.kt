package ru.g000sha256.reduktor

import io.reactivex.rxjava3.core.Observable

interface Middleware<Action, State> {

    fun beforeReduce(actionObservable: Observable<Action>, stateAccessor: () -> State): Observable<Action>

    fun afterReduce(actionObservable: Observable<Action>, stateAccessor: () -> State): Observable<Action>

}