package g000sha256.reduktor.core

fun interface Reducer<ACTION, STATE> {

    fun STATE.invoke(action: ACTION): STATE

}