package ru.g000sha256.reduktor.demo.screen.main.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.reactivex.rxjava3.functions.Consumer
import ru.g000sha256.reduktor.demo.R
import ru.g000sha256.reduktor.demo.screen.main.MainAction
import ru.g000sha256.reduktor.demo.screen.main.MainItem

class MainErrorViewHolder(private val actionConsumer: Consumer<MainAction>, view: View) : RecyclerView.ViewHolder(view) {

    private val messageTextView = view.findViewById<TextView>(R.id.message_text_view)

    init {
        val layoutParams = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        layoutParams.isFullSpan = true
        view
                .findViewById<View>(R.id.button_text_view)
                .setOnClickListener {
                    val action = MainAction.Click.Retry()
                    actionConsumer.accept(action)
                }
    }

    fun bind(item: MainItem.Error) {
        messageTextView.text = item.text
    }

}