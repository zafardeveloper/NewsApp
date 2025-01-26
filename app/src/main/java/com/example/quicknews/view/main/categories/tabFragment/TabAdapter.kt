package com.example.quicknews.view.main.categories.tabFragment

import android.annotation.SuppressLint
import android.util.Log
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.databinding.EachItemBinding
import com.example.quicknews.databinding.RowItemNewsArticleBinding
import com.example.quicknews.databinding.RowItemNewsArticleShimmerBinding
import com.example.quicknews.databinding.RowItemNewsCategoriesHorizontalShimmerBinding
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.util.StartSnapHelper
import com.example.quicknews.util.Util.Companion.formatDate

class TabAdapter(private val listener: OnItemClickListener<Article>) :
    RecyclerView.Adapter<ViewHolder>() {

    private var isLoading = true
    private val shimmerItemsCount = 4

    companion object {
        private const val VIEW_TYPE_HORIZONTAL = 0
        private const val VIEW_TYPE_VERTICAL = 1
        private const val VIEW_TYPE_SHIMMER_HORIZONTAL = 2
        private const val VIEW_TYPE_SHIMMER = 3
    }

    private val horizontalScrollPositions = SparseIntArray()

    inner class VerticalArticleViewHolder(binding: RowItemNewsArticleBinding) :
        ViewHolder(binding.root) {
        private val title = binding.tvTitle
        private val source = binding.tvSource
        private val publishedAt = binding.tvPublishedAt
        private val image = binding.ivArticleImage
        fun bind(article: Article) {
            title.text = article.title
            source.text = article.source?.name

            publishedAt.text =
                formatDate(article.publishedAt!!, "yyyy-MM-dd'T'HH:mm:ss'Z'")
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

        fun bind(articleList: List<Article>, listener: OnItemClickListener<Article>, position: Int) {
            val childAdapter = TabChildAdapter(articleList, listener)
            childRecyclerView.apply {
                adapter = childAdapter
                layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                isNestedScrollingEnabled = true

                val savedPosition = horizontalScrollPositions[position]
                (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(savedPosition, 0)

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            val currentPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                            horizontalScrollPositions.put(position, currentPosition)
                        }
                    }
                })

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

    inner class ShimmerViewHolder(binding: RowItemNewsArticleShimmerBinding) :
        ViewHolder(binding.root) {
        val shimmerLayout = binding.shimmerLayout
    }

    inner class ShimmerViewHolderHorizontal(binding: RowItemNewsCategoriesHorizontalShimmerBinding) :
        ViewHolder(binding.root) {
        val shimmerLayout = binding.shimmerLayout
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
        return when (position) {
            0 -> {
                if (isLoading) {
                    VIEW_TYPE_SHIMMER_HORIZONTAL
                } else {
                    VIEW_TYPE_HORIZONTAL
                }
            }

            else -> {
                if (isLoading) {
                    VIEW_TYPE_SHIMMER
                } else {
                    VIEW_TYPE_VERTICAL
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_VERTICAL -> {
                val binding = RowItemNewsArticleBinding.inflate(
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

            VIEW_TYPE_SHIMMER -> {
                val binding = RowItemNewsArticleShimmerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ShimmerViewHolder(binding)
            }

            VIEW_TYPE_SHIMMER_HORIZONTAL -> {
                val binding = RowItemNewsCategoriesHorizontalShimmerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ShimmerViewHolderHorizontal(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) shimmerItemsCount else differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {

            VIEW_TYPE_HORIZONTAL -> {
                val article = differ.currentList[position]
                val horizontalHolder = holder as HorizontalArticleViewHolder
                horizontalHolder.bind(differ.currentList.take(10), listener, position)
                horizontalHolder.itemView.setOnClickListener {
                    listener.onClick(article)
                }
                horizontalHolder.itemView.setOnLongClickListener {
                    listener.onLongClick(it, article)
                    true
                }
            }

            VIEW_TYPE_VERTICAL -> {
                val verticalHolder = holder as VerticalArticleViewHolder
                val adjustedPosition = position - 1
                if (adjustedPosition in 0 until differ.currentList.size) {
                    val article = differ.currentList[adjustedPosition + 1]
                    verticalHolder.bind(article)
                    verticalHolder.itemView.setOnClickListener {
                        listener.onClick(article)
                    }
                    verticalHolder.itemView.setOnLongClickListener {
                        listener.onLongClick(it, article)
                        true
                    }
                } else {
                    Log.e("TabAdapter", "Invalid position: $adjustedPosition")
                }
            }

            VIEW_TYPE_SHIMMER -> {
                val shimmerHolder = holder as ShimmerViewHolder
                shimmerHolder.shimmerLayout.startShimmer()
            }

            VIEW_TYPE_SHIMMER_HORIZONTAL -> {
                val shimmerHolder = holder as ShimmerViewHolderHorizontal
                shimmerHolder.shimmerLayout.startShimmer()
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newMovieList: List<Article>) {
        isLoading = false
        differ.submitList(newMovieList) {
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showLoading() {
        if (!isLoading) {
            isLoading = true
        }
        notifyDataSetChanged()
    }
}