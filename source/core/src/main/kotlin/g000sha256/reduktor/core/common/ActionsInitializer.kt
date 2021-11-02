package g000sha256.reduktor.core.common

import g000sha256.reduktor.core.Actions
import g000sha256.reduktor.core.Environment
import g000sha256.reduktor.core.Initializer

class ActionsInitializer<ACTION, STATE> : Initializer<ACTION, STATE> {

    val actions: Actions<ACTION>

    private val any = Any()

    private var _actions: Actions<ACTION>? = null

    init {
        actions = object : Actions<ACTION> {

            override fun post(action: ACTION) {
                post { it.post(action) }
            }

            override fun post(vararg actions: ACTION) {
                post { it.post(*actions) }
            }

            override fun post(actions: Iterable<ACTION>) {
                post { it.post(actions) }
            }

            private fun post(callback: (Actions<ACTION>) -> Unit) {
                synchronized(any) {
                    val actions = _actions ?: throw IllegalStateException("Initializer has not been called yet")
                    callback(actions)
                }
            }

        }
    }

    override fun Environment<ACTION>.invoke(initialState: STATE) {
        synchronized(any) {
            if (_actions != null) throw IllegalStateException("Initializer has already been called")
            _actions = actions
        }
    }

}