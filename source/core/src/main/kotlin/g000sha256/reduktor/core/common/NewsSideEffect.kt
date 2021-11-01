package g000sha256.reduktor.core.common

import g000sha256.reduktor.core.Environment
import g000sha256.reduktor.core.SideEffect

inline fun <ACTION, STATE, reified NEWS> NewsSideEffect(
    noinline newsCallback: (news: NEWS) -> Unit
): NewsSideEffect<ACTION, STATE, NEWS> {
    return NewsSideEffect(NEWS::class.java, newsCallback)
}

class NewsSideEffect<ACTION, STATE, NEWS> : SideEffect<ACTION, STATE> {

    private val mapper: (ACTION, STATE) -> NEWS?
    private val newsCallback: (NEWS) -> Unit

    constructor(clazz: Class<NEWS>, newsCallback: (news: NEWS) -> Unit) {
        this.newsCallback = newsCallback
        mapper = { action, _ -> mapClass(clazz, action) }
    }

    constructor(mapper: (action: ACTION, state: STATE) -> NEWS?, newsCallback: (news: NEWS) -> Unit) {
        this.mapper = mapper
        this.newsCallback = newsCallback
    }

    override fun Environment<ACTION>.invoke(action: ACTION, state: STATE) {
        val news = mapper(action, state) ?: return
        newsCallback(news)
    }

    private fun mapClass(clazz: Class<NEWS>, action: ACTION): NEWS? {
        val isInstance = clazz.isInstance(action)
        return if (isInstance) action as NEWS else null
    }

}