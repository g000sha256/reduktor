package ru.g000sha256.reduktor

import io.reactivex.rxjava3.subjects.BehaviorSubject

class StateAccessor<State> internal constructor(private val behaviorSubject: BehaviorSubject<State>) {

    val state: State
        get() = behaviorSubject.value

}