package g000sha256.reduktor.rxjava3

import g000sha256.reduktor.core.Initializer
import g000sha256.reduktor.core.Logger
import g000sha256.reduktor.core.Reducer
import g000sha256.reduktor.core.SideEffect
import g000sha256.reduktor.core.Store
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.BehaviorProcessor

class Store<ACTION, STATE>(
    initialState: STATE,
    reducer: Reducer<ACTION, STATE>,
    initializers: Iterable<Initializer<ACTION, STATE>> = emptyList(),
    sideEffects: Iterable<SideEffect<ACTION, STATE>> = emptyList(),
    logger: Logger = Logger {}
) {

    val states: Flowable<STATE>

    private val store: Store<ACTION, STATE>

    init {
        val behaviorProcessor = BehaviorProcessor.createDefault(initialState)
        states = behaviorProcessor.onBackpressureLatest()
        store = Store(initialState, reducer, initializers, sideEffects, logger, behaviorProcessor::onNext)
    }

    fun release() {
        store.release()
    }

}