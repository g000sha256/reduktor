package g000sha256.reduktor.coroutines

import g000sha256.reduktor.core.Task
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class TaskImpl(
    private val coroutineContext: CoroutineContext,
    private val coroutineScope: CoroutineScope,
    private val block: suspend CoroutineScope.() -> Unit
) : Task() {

    private var job: Job? = null

    override fun onCancel() {
        val job = job ?: return
        val internalCancellationException = InternalCancellationException()
        job.cancel(internalCancellationException)
        this.job = null
    }

    override fun onStart(onTerminate: () -> Unit) {
        val job = coroutineScope.launch(coroutineContext, CoroutineStart.LAZY, block)
        this.job = job
        job.invokeOnCompletion { if (it !is InternalCancellationException) onTerminate() }
        job.start()
    }

    private class InternalCancellationException : CancellationException()

}