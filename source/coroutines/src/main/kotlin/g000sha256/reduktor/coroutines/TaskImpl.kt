package g000sha256.reduktor.coroutines

import g000sha256.reduktor.core.Task
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

internal class TaskImpl(
    coroutineContext: CoroutineContext,
    coroutineScope: CoroutineScope,
    block: suspend CoroutineScope.() -> Unit
) : Task {

    override val status: Task.Status
        get() = synchronized(any) { _status }

    private val any = Any()
    private val job = coroutineScope.launch(coroutineContext, CoroutineStart.LAZY, block)

    private var _status = Task.Status.INITIALIZED

    override fun cancel() {
        synchronized(any) {
            _status.checkNotCancelled()
            _status.checkNotCompleted()
            _status.checkStarted()
            _status = Task.Status.CANCELLED
            job.cancel()
        }
    }

    override fun start(onFinish: () -> Unit) {
        synchronized(any) {
            _status.checkNotCancelled()
            _status.checkNotCompleted()
            _status.checkNotStarted()
            _status = Task.Status.STARTED
            job.invokeOnCompletion { finish(onFinish) }
            job.start()
        }
    }

    private fun finish(onFinish: () -> Unit) {
        synchronized(any) {
            if (_status == Task.Status.STARTED) _status = Task.Status.COMPLETED
            onFinish()
        }
    }

}