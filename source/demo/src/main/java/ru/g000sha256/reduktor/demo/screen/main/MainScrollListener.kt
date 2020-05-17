package ru.g000sha256.reduktor.demo.screen.main

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.reactivex.rxjava3.functions.Consumer

class MainScrollListener(
        private val actionConsumer: Consumer<MainAction>,
        private val staggeredGridLayoutManager: StaggeredGridLayoutManager
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val lastVisibleItemPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null)
        val lastVisibleItemPosition = lastVisibleItemPositions[0]
        if (lastVisibleItemPosition + 1 + 3 * staggeredGridLayoutManager.spanCount >= staggeredGridLayoutManager.itemCount) {
            val action = MainAction.StartLoading.Next()
            actionConsumer.accept(action)
        }
    }

}