package com.example.quicknews.view.main.categories.tabFragment

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.databinding.EachItemBinding
import com.example.quicknews.databinding.RowItemNewsSearchBinding
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.StartSnapHelper
import com.example.quicknews.util.Util.Companion.formatDate

open class TabAdapter(private val listener: Listener) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_VERTICAL = 0
        private const val VIEW_TYPE_HORIZONTAL = 1
    }

    inner class VerticalArticleViewHolder(binding: RowItemNewsSearchBinding) :
        ViewHolder(binding.root) {
        private val title = binding.tvTitle
        private val source = binding.tvSource
        private val publishedAt = binding.tvPublishedAt
        private val image = binding.ivArticleImage
        val constraintLayout = binding.constraintLayout
        fun bind(article: Article) {
            title.text = article.title
            source.text = article.source?.name

            publishedAt.text =
                formatDate(article.publishedAt!!, "yyyy-MM-dd'T'HH:mm:ss'Z'", "dd MMMM")
            if (article.urlToImage.isNullOrEmpty()) {
                image.setImageResource(R.drawable.ic_no_image)
            } else {
                Glide.with(itemView.context).load(article.urlToImage).into(image)
            }
        }
    }


    inner class HorizontalArticleViewHolder(private val binding: EachItemBinding) :
        ViewHolder(binding.root) {
        private val childRecyclerView = binding.childRecyclerView

        init {
            val snapHelper = StartSnapHelper()
            snapHelper.attachToRecyclerView(childRecyclerView)
        }

        fun bind(articleList: List<Article>, listener: Listener) {
            val childAdapter = TabChildAdapter(articleList, listener)
            childRecyclerView.apply {
                adapter = childAdapter
                layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                isNestedScrollingEnabled = true
                addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    override fun onInterceptTouchEvent(
                        rv: RecyclerView,
                        event: MotionEvent
                    ): Boolean {
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                childRecyclerView.parent?.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

                })
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HORIZONTAL else VIEW_TYPE_VERTICAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_VERTICAL -> {
                val binding = RowItemNewsSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VerticalArticleViewHolder(binding)
            }

            VIEW_TYPE_HORIZONTAL -> {
                val binding = EachItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HorizontalArticleViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {

            VIEW_TYPE_HORIZONTAL -> {
                val article = differ.currentList[position]
                val horizontalHolder = holder as HorizontalArticleViewHolder
                horizontalHolder.bind(differ.currentList.take(10), listener)
                horizontalHolder.itemView.setOnClickListener {
                    listener.onClick(article)
                }
                horizontalHolder.itemView.setOnLongClickListener {
                    listener.onLongClick(it, article)
                    true
                }
            }

            VIEW_TYPE_VERTICAL -> {
                val article = differ.currentList[position]
                val verticalHolder = holder as VerticalArticleViewHolder
                val verticalItems = differ.currentList.drop(10)[position]
                verticalHolder.bind(verticalItems)
                verticalHolder.constraintLayout.startAnimation(
                    AnimationUtils.loadAnimation(holder.itemView.context, R.anim.emergence)
                )
                verticalHolder.itemView.setOnClickListener {
                    listener.onClick(article)
                }
                verticalHolder.itemView.setOnLongClickListener {
                    listener.onLongClick(it, article)
                    true
                }
            }

        }
    }

    interface Listener {
        fun onClick(item: Article)

        fun onLongClick(view: View, item: Article)
    }
}