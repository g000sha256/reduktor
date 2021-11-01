package g000sha256.reduktor.rxjava2.ext

import g000sha256.reduktor.core.Task
import g000sha256.reduktor.rxjava2.TaskImpl
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

fun <T> Maybe<T>.toTask(
    onSuccess: Consumer<T>,
    onError: Consumer<Throwable>,
    onComplete: Action = Action {}
): Task {
    return object : TaskImpl() {

        override fun createDisposable(onFinish: () -> Unit): Disposable {
            return doAfterTerminate(onFinish)
                .doOnDispose(onFinish)
                .subscribe(onSuccess, onError, onComplete)
        }

    }
}