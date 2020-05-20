package ru.g000sha256.reduktor.demo.screen.main.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ru.g000sha256.reduktor.demo.R
import ru.g000sha256.reduktor.demo.screen.main.MainAction
import ru.g000sha256.reduktor.demo.screen.main.MainItem

class MainUserViewHolder(
        private val drawableTransitionOptions: DrawableTransitionOptions,
        private val requestManager: RequestManager,
        private val view: View,
        private val actionConsumer: (MainAction) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val imageView = view.findViewById<ImageView>(R.id.image_view)
    private val textView = view.findViewById<TextView>(R.id.text_view)

    fun bind(item: MainItem.User) {
        textView.text = item.login
        requestManager
                .load(item.avatarUrl)
                .placeholder(R.color.white_8)
                .transition(drawableTransitionOptions)
                .into(imageView)
        view.setOnClickListener {
            val action = MainAction.Click.Item(item.id)
            actionConsumer(action)
        }
    }

}