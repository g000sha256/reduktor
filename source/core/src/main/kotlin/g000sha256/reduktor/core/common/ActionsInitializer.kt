package g000sha256.reduktor.core.common

import g000sha256.reduktor.core.Actions
import g000sha256.reduktor.core.Environment
import g000sha256.reduktor.core.Initializer

class ActionsInitializer<ACTION, STATE> : Initializer<ACTION, STATE> {

    val actions: Actions<ACTION>
        get() = synchronized(any) { _actions ?: throw IllegalStateException("Initializer has not been called yet") }

    private val any = Any()

    private var _actions: Actions<ACTION>? = null

    override fun Environment<ACTION>.invoke(initialState: STATE) {
        synchronized(any) {
            if (_actions != null) throw IllegalStateException("Initializer has already been called")
            _actions = actions
        }
    }

}