package g000sha256.reduktor.coroutines

import g000sha256.reduktor.core.Initializer
import g000sha256.reduktor.core.Logger
import g000sha256.reduktor.core.Reducer
import g000sha256.reduktor.core.SideEffect
import g000sha256.reduktor.core.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Store<ACTION, STATE>(
    initialState: STATE,
    reducer: Reducer<ACTION, STATE>,
    initializers: Iterable<Initializer<ACTION, STATE>> = emptyList(),
    sideEffects: Iterable<SideEffect<ACTION, STATE>> = emptyList(),
    logger: Logger = Logger {}
) {

    val states: StateFlow<STATE>

    private val store: Store<ACTION, STATE>

    init {
        val mutableStateFlow = MutableStateFlow(initialState)
        states = mutableStateFlow
        store = Store(initialState, reducer, initializers, sideEffects, logger) { mutableStateFlow.value = it }
    }

    fun release() {
        store.release()
    }

}