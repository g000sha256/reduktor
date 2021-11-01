package g000sha256.reduktor.rxjava2.ext

import g000sha256.reduktor.core.Task
import g000sha256.reduktor.rxjava2.TaskImpl
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

fun <T> Single<T>.toTask(onSuccess: Consumer<T>, onError: Consumer<Throwable>): Task {
    return object : TaskImpl() {

        override fun createDisposable(onFinish: () -> Unit): Disposable {
            return doAfterTerminate(onFinish)
                .doOnDispose(onFinish)
                .subscribe(onSuccess, onError)
        }

    }
}