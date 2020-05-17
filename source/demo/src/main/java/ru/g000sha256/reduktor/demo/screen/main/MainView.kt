package ru.g000sha256.reduktor.demo.screen.main

import android.app.AlertDialog
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Consumer
import ru.g000sha256.reduktor.demo.R
import ru.g000sha256.reduktor.demo.extension.plusAssign
import ru.g000sha256.schedulers_factory.SchedulersFactory
import kotlin.math.max

private const val ID_DATA = 1
private const val ID_ERROR = 2
private const val ID_LOADING = 3

class MainView(
        private val actionConsumer: Consumer<MainAction>,
        private val viewEventObservable: Observable<MainViewEvent>,
        private val viewStateObservable: Observable<MainViewState>,
        private val requestManager: RequestManager,
        private val schedulersFactory: SchedulersFactory,
        rootViewGroup: ViewGroup
) {

    private val compositeDisposable = CompositeDisposable()
    private val context = rootViewGroup.context
    private val contentViewGroup: ViewGroup

    private var dialog: Dialog? = null

    init {
        val view = View.inflate(context, R.layout.main, rootViewGroup)
        contentViewGroup = view.findViewById(R.id.content_frame_layout)
    }

    fun onAttach() {
        compositeDisposable += viewEventObservable
                .observeOn(schedulersFactory.mainDeferredScheduler)
                .subscribe {
                    when (it) {
                        is MainViewEvent.Show.Dialog -> showDialog(it.userId)
                        is MainViewEvent.Show.SnackBar -> showSnackBar(it.text)
                        is MainViewEvent.Show.Toast -> showToast(it.text)
                    }
                }
        compositeDisposable += viewStateObservable
                .observeOn(schedulersFactory.mainImmediateScheduler)
                .subscribe {
                    when (it) {
                        is MainViewState.Data -> showData(it.isRefreshing, it.items)
                        is MainViewState.Error -> showError(it.text)
                        is MainViewState.Loading -> showLoading()
                    }
                }
        val action = MainAction.ViewAttached()
        actionConsumer.accept(action)
    }

    fun onDetach() {
        compositeDisposable.clear()
    }

    private fun calculateColumnsCount(): Int {
        val resources = contentViewGroup.resources
        val width = resources.displayMetrics.widthPixels - 2 * resources.getDimensionPixelSize(R.dimen.padding)
        val columnsCount = width / resources.getDimensionPixelSize(R.dimen.card_width)
        return max(columnsCount, 2)
    }

    private fun showData(isRefreshing: Boolean, items: List<MainItem>) {
        if (contentViewGroup.tag == ID_DATA) {
            val recyclerView = contentViewGroup.findViewById<RecyclerView>(R.id.recycler_view)
            val adapter = recyclerView.adapter as MainAdapter
            adapter.setItems(items)
            adapter.notifyDataSetChanged()
            val swipeRefreshLayout = contentViewGroup.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
            swipeRefreshLayout.isRefreshing = isRefreshing
            val scrollListener = MainScrollListener(actionConsumer, recyclerView.layoutManager as StaggeredGridLayoutManager)
            scrollListener.onScrolled(recyclerView, 0, 0)
        } else {
            contentViewGroup.tag = ID_DATA
            contentViewGroup.removeAllViews()
            val view = View.inflate(context, R.layout.main_data, contentViewGroup)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
            val layoutInflater = LayoutInflater.from(context)
            val adapter = MainAdapter(actionConsumer, layoutInflater, requestManager)
            recyclerView.adapter = adapter
            adapter.setItems(items)
            val columnsCount = calculateColumnsCount()
            val staggeredGridLayoutManager = StaggeredGridLayoutManager(columnsCount, StaggeredGridLayoutManager.VERTICAL)
            recyclerView.layoutManager = staggeredGridLayoutManager
            val scrollListener = MainScrollListener(actionConsumer, staggeredGridLayoutManager)
            recyclerView.addOnScrollListener(scrollListener)
            val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
            swipeRefreshLayout.isRefreshing = isRefreshing
            swipeRefreshLayout.setColorSchemeResources(R.color.lime)
            swipeRefreshLayout.setOnRefreshListener {
                val action = MainAction.StartLoading.Reload()
                actionConsumer.accept(action)
            }
        }
    }

    private fun showDialog(userId: Long) {
        dialog?.dismiss()
        val dialog = AlertDialog.Builder(context)
                .setMessage(R.string.dialog_message)
                .setNegativeButton(R.string.dialog_button_no) { _, _ ->
                    val action = MainAction.Click.Dialog(userId = null)
                    actionConsumer.accept(action)
                }
                .setPositiveButton(R.string.dialog_button_yes) { _, _ ->
                    val action = MainAction.Click.Dialog(userId)
                    actionConsumer.accept(action)
                }
                .create()
        dialog.setOnDismissListener { this.dialog = null }
        dialog.setOnCancelListener {
            val action = MainAction.Click.Dialog(userId = null)
            actionConsumer.accept(action)
        }
        dialog.show()
        this.dialog = dialog
    }

    private fun showError(text: String) {
        if (contentViewGroup.tag == ID_ERROR) return
        contentViewGroup.tag = ID_ERROR
        contentViewGroup.removeAllViews()
        val view = View.inflate(context, R.layout.main_error, contentViewGroup)
        val messageTextView = view.findViewById<TextView>(R.id.message_text_view)
        messageTextView.text = text
        view
                .findViewById<View>(R.id.button_text_view)
                .setOnClickListener {
                    val action = MainAction.Click.Retry()
                    actionConsumer.accept(action)
                }
    }

    private fun showLoading() {
        if (contentViewGroup.tag == ID_LOADING) return
        contentViewGroup.tag = ID_LOADING
        contentViewGroup.removeAllViews()
        View.inflate(context, R.layout.main_loading, contentViewGroup)
    }

    private fun showSnackBar(text: String) {
        val snackBar = Snackbar
                .make(contentViewGroup, text, Snackbar.LENGTH_SHORT)
                .setAction(R.string.error_button) {
                    val action = MainAction.StartLoading.Reload()
                    actionConsumer.accept(action)
                }
        val textView = snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = Int.MAX_VALUE
        snackBar.show()
    }

    private fun showToast(text: String) {
        Toast
                .makeText(context, text, Toast.LENGTH_SHORT)
                .show()
    }

}