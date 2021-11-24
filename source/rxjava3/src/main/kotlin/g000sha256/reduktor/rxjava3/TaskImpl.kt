package g000sha256.reduktor.rxjava3

import g000sha256.reduktor.core.Task
import io.reactivex.rxjava3.disposables.Disposable

internal abstract class TaskImpl : Task() {

    private var disposable: Disposable? = null

    final override fun onCancel() {
        val disposable = disposable ?: return
        if (!disposable.isDisposed) disposable.dispose()
        this.disposable = null
    }

    final override fun onStart(onTerminate: () -> Unit) {
        disposable = createDisposable(onTerminate)
    }

    protected abstract fun createDisposable(onTerminate: () -> Unit): Disposable

}