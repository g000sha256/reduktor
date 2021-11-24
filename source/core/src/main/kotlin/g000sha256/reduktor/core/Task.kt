package g000sha256.reduktor.core

abstract class Task {

    private var status = Status.INITIALIZED

    protected abstract fun onCancel()

    protected abstract fun onStart(onTerminate: () -> Unit)

    internal fun cancel() {
        checkNotCancelled()
        checkNotCompleted()
        checkStarted()
        status = Status.CANCELLED
        onCancel()
    }

    internal fun check() {
        checkNotCancelled()
        checkNotCompleted()
        checkNotStarted()
    }

    internal fun completeIfNeeded() {
        if (status == Status.STARTED) status = Status.COMPLETED
    }

    internal fun start(onTerminate: () -> Unit) {
        checkNotCancelled()
        checkNotCompleted()
        checkNotStarted()
        status = Status.STARTED
        onStart(onTerminate)
        if (status == Status.CANCELLED) onCancel()
    }

    private fun checkNotCancelled() {
        if (status == Status.CANCELLED) throw IllegalStateException("Task has already been cancelled")
    }

    private fun checkNotCompleted() {
        if (status == Status.COMPLETED) throw IllegalStateException("Task has already been completed")
    }

    private fun checkNotStarted() {
        if (status == Status.STARTED) throw IllegalStateException("Task has already been started")
    }

    private fun checkStarted() {
        if (status == Status.INITIALIZED) throw IllegalStateException("Task has not been started yet")
    }

    private enum class Status {

        CANCELLED,
        COMPLETED,
        INITIALIZED,
        STARTED

    }

}