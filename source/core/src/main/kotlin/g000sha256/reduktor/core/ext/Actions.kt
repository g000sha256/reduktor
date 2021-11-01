package g000sha256.reduktor.core.ext

import g000sha256.reduktor.core.Actions

infix fun <ACTION> Actions<ACTION>.post(action: ACTION) {
    post(action)
}

infix fun <ACTION> Actions<ACTION>.post(actions: Array<ACTION>) {
    post(*actions)
}

infix fun <ACTION> Actions<ACTION>.post(actions: Iterable<ACTION>) {
    post(actions)
}