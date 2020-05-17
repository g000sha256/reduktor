package ru.g000sha256.reduktor.demo.screen.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.reactivex.rxjava3.functions.Consumer
import ru.g000sha256.reduktor.demo.R
import ru.g000sha256.reduktor.demo.screen.main.adapter.MainErrorViewHolder
import ru.g000sha256.reduktor.demo.screen.main.adapter.MainLoadingViewHolder
import ru.g000sha256.reduktor.demo.screen.main.adapter.MainUserViewHolder

private const val TYPE_ERROR = 1
private const val TYPE_LOADING = 2
private const val TYPE_USER = 3

class MainAdapter(
        private val actionConsumer: Consumer<MainAction>,
        private val layoutInflater: LayoutInflater,
        private val requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = ArrayList<MainItem>()
    private val drawableTransitionOptions = DrawableTransitionOptions.withCrossFade()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        when (item) {
            is MainItem.Error -> return TYPE_ERROR
            is MainItem.Loading -> return TYPE_LOADING
            is MainItem.User -> return TYPE_USER
            else -> throw IllegalArgumentException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_ERROR -> {
                val view = layoutInflater.inflate(R.layout.main_data_error, parent, false)
                return MainErrorViewHolder(actionConsumer, view)
            }
            TYPE_LOADING -> {
                val view = layoutInflater.inflate(R.layout.main_data_loading, parent, false)
                return MainLoadingViewHolder(view)
            }
            TYPE_USER -> {
                val view = layoutInflater.inflate(R.layout.main_data_user, parent, false)
                return MainUserViewHolder(actionConsumer, drawableTransitionOptions, requestManager, view)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MainErrorViewHolder -> {
                val item = items[position] as MainItem.Error
                holder.bind(item)
            }
            is MainUserViewHolder -> {
                val item = items[position] as MainItem.User
                holder.bind(item)
            }
        }
    }

    fun setItems(items: List<MainItem>) {
        this.items.clear()
        this.items.addAll(items)
    }

}