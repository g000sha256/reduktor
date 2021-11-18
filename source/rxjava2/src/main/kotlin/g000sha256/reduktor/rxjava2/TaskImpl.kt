package g000sha256.reduktor.rxjava2

import g000sha256.reduktor.core.Task
import io.reactivex.disposables.Disposable

internal abstract class TaskImpl : Task {

    override val status: Task.Status
        get() = synchronized(any) { _status }

    private val any = Any()

    private var _status = Task.Status.INITIALIZED
    private var disposable: Disposable? = null

    override fun cancel() {
        synchronized(any) {
            _status.checkNotCancelled()
            _status.checkNotCompleted()
            _status.checkStarted()
            _status = Task.Status.CANCELLED
            disposable?.disposeIfNeeded()
        }
    }

    override fun start(onFinish: () -> Unit) {
        synchronized(any) {
            _status.checkNotCancelled()
            _status.checkNotCompleted()
            _status.checkNotStarted()
            _status = Task.Status.STARTED
            disposable = createDisposable { finish(onFinish) }
            if (_status == Task.Status.CANCELLED) disposable?.disposeIfNeeded()
        }
    }

    protected abstract fun createDisposable(onFinish: () -> Unit): Disposable

    private fun finish(onFinish: () -> Unit) {
        synchronized(any) { if (_status == Task.Status.STARTED) _status = Task.Status.COMPLETED }
        onFinish()
    }

    private fun Disposable.disposeIfNeeded() {
        if (!isDisposed) dispose()
        disposable = null
    }

}