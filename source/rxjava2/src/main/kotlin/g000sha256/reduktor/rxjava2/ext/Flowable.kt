package g000sha256.reduktor.rxjava2.ext

import g000sha256.reduktor.core.Task
import g000sha256.reduktor.rxjava2.TaskImpl
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

fun <T> Flowable<T>.toTask(
    onNext: Consumer<T>,
    onError: Consumer<Throwable>,
    onComplete: Action = Action {}
): Task {
    return object : TaskImpl() {

        override fun createDisposable(onTerminate: () -> Unit): Disposable {
            return doAfterTerminate(onTerminate)
                .subscribe(onNext, onError, onComplete)
        }

    }
}