package g000sha256.reduktor.rxjava3.ext

import g000sha256.reduktor.core.Task
import g000sha256.reduktor.rxjava3.TaskImpl
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Action
import io.reactivex.rxjava3.functions.Consumer

fun <T> Observable<T>.toTask(
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