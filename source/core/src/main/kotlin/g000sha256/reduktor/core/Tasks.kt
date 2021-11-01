package g000sha256.reduktor.core

interface Tasks {

    fun add(task: Task)

    fun add(key: String, task: Task)

    fun clear()

    fun clear(key: String)

}