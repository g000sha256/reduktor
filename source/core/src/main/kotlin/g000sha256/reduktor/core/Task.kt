package g000sha256.reduktor.core

interface Task {

    val status: Status

    fun cancel()

    fun start(onFinish: () -> Unit)

    enum class Status {

        CANCELLED,
        COMPLETED,
        INITIALIZED,
        STARTED;

        fun checkNotCancelled() {
            if (this == CANCELLED) throw IllegalStateException("Task has already been cancelled")
        }

        fun checkNotCompleted() {
            if (this == COMPLETED) throw IllegalStateException("Task has already been completed")
        }

        fun checkNotStarted() {
            if (this == STARTED) throw IllegalStateException("Task has already been started")
        }

        fun checkStarted() {
            if (this == INITIALIZED) throw IllegalStateException("Task has not been started yet")
        }

    }

}