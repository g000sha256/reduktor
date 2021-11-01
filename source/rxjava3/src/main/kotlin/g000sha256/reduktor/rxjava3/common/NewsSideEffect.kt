package g000sha256.reduktor.rxjava3.common

import g000sha256.reduktor.core.Environment
import g000sha256.reduktor.core.SideEffect
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.PublishProcessor
import g000sha256.reduktor.core.common.NewsSideEffect as CoreNewsSideEffect

inline fun <ACTION, STATE, reified NEWS> NewsSideEffect(): NewsSideEffect<ACTION, STATE, NEWS> {
    return NewsSideEffect(NEWS::class.java)
}

class NewsSideEffect<ACTION, STATE, NEWS> : SideEffect<ACTION, STATE> {

    val news: Flowable<NEWS>
        get() = publishProcessor

    private val coreNewsSideEffect: CoreNewsSideEffect<ACTION, STATE, NEWS>
    private val publishProcessor = PublishProcessor.create<NEWS>()

    constructor(clazz: Class<NEWS>) {
        coreNewsSideEffect = CoreNewsSideEffect(clazz, publishProcessor::onNext)
    }

    constructor(mapper: (action: ACTION, state: STATE) -> NEWS?) {
        coreNewsSideEffect = CoreNewsSideEffect(mapper, publishProcessor::onNext)
    }

    override fun Environment<ACTION>.invoke(action: ACTION, state: STATE) {
        coreNewsSideEffect.apply { invoke(action, state) }
    }

}