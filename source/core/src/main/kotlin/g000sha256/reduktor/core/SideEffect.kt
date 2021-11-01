package g000sha256.reduktor.core

fun interface SideEffect<ACTION, STATE> {

    fun Environment<ACTION>.invoke(action: ACTION, state: STATE)

}