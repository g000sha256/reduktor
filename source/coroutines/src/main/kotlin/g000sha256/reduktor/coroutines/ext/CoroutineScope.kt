package g000sha256.reduktor.coroutines.ext

import g000sha256.reduktor.core.Task
import g000sha256.reduktor.coroutines.TaskImpl
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope

fun CoroutineScope.newTask(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): Task {
    return TaskImpl(context, coroutineScope = this, block)
}