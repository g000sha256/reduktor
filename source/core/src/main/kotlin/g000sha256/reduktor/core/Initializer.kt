package g000sha256.reduktor.core

fun interface Initializer<ACTION, STATE> {

    fun Environment<ACTION>.invoke(initialState: STATE)

}