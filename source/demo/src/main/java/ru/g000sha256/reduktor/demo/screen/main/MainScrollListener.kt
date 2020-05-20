package ru.g000sha256.reduktor.demo.screen.main

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MainScrollListener(
        private val staggeredGridLayoutManager: StaggeredGridLayoutManager,
        private val actionConsumer: (MainAction) -> Unit
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val lastVisibleItemPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(null)
        val lastVisibleItemPosition = lastVisibleItemPositions[0]
        if (lastVisibleItemPosition + 1 + 3 * staggeredGridLayoutManager.spanCount >= staggeredGridLayoutManager.itemCount) {
            val action = MainAction.StartLoading.Next()
            actionConsumer(action)
        }
    }

}