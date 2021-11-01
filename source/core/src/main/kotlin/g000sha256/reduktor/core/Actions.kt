package g000sha256.reduktor.core

interface Actions<ACTION> {

    fun post(action: ACTION)

    fun post(vararg actions: ACTION)

    fun post(actions: Iterable<ACTION>)

}