package g000sha256.reduktor.core

interface Environment<ACTION> {

    val actions: Actions<ACTION>
    val tasks: Tasks

}