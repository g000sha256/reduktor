package g000sha256.reduktor.core.ext

import g000sha256.reduktor.core.Task
import g000sha256.reduktor.core.Tasks

infix fun Tasks.clear(key: String) {
    clear(key)
}

operator fun Tasks.plusAssign(task: Task) {
    add(task)
}

operator fun Tasks.set(key: String, task: Task) {
    add(key, task)
}