package ru.g000sha256.reduktor

interface Reducer<Action, State> {

    fun reduce(action: Action, state: State): State

}