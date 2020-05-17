package ru.g000sha256.reduktor.demo.screen.main.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MainLoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    init {
        val layoutParams = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        layoutParams.isFullSpan = true
    }

}