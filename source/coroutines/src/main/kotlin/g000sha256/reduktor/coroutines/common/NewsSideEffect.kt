package g000sha256.reduktor.coroutines.common

import g000sha256.reduktor.core.Environment
import g000sha256.reduktor.core.SideEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import g000sha256.reduktor.core.common.NewsSideEffect as CoreNewsSideEffect

inline fun <ACTION, STATE, reified NEWS> NewsSideEffect(): NewsSideEffect<ACTION, STATE, NEWS> {
    return NewsSideEffect(NEWS::class.java)
}

class NewsSideEffect<ACTION, STATE, NEWS> : SideEffect<ACTION, STATE> {

    val news: Flow<NEWS>
        get() = mutableSharedFlow

    private val coreNewsSideEffect: CoreNewsSideEffect<ACTION, STATE, NEWS>
    private val mutableSharedFlow = MutableSharedFlow<NEWS>()

    constructor(clazz: Class<NEWS>) {
        coreNewsSideEffect = CoreNewsSideEffect(clazz, ::emit)
    }

    constructor(mapper: (action: ACTION, state: STATE) -> NEWS?) {
        coreNewsSideEffect = CoreNewsSideEffect(mapper, ::emit)
    }

    override fun Environment<ACTION>.invoke(action: ACTION, state: STATE) {
        coreNewsSideEffect.apply { invoke(action, state) }
    }

    private fun emit(news: NEWS) {
        runBlocking { mutableSharedFlow.emit(news) }
    }

}