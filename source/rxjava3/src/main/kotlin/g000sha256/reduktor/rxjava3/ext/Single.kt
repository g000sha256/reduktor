package g000sha256.reduktor.rxjava3.ext

import g000sha256.reduktor.core.Task
import g000sha256.reduktor.rxjava3.TaskImpl
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

fun <T> Single<T>.toTask(onSuccess: Consumer<T>, onError: Consumer<Throwable>): Task {
    return object : TaskImpl() {

        override fun createDisposable(onFinish: () -> Unit): Disposable {
            return doAfterTerminate(onFinish)
                .doOnDispose(onFinish)
                .subscribe(onSuccess, onError)
        }

    }
}