package com.example.quicknews.view.main.search.queryAdapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.databinding.RowItemNewsArticleBinding
import com.example.quicknews.databinding.RowItemNewsArticleShimmerBinding
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.util.Util.Companion.formatDate

class SearchQueryAdapter(private val listener: OnItemClickListener<Article>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading = true
    private val shimmerItemsCount = 6

    companion object {
        const val VIEW_TYPE_SHIMMER = 0
        const val VIEW_TYPE_NEWS = 1
    }

    inner class ArticleViewHolder(binding: RowItemNewsArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvTitle
        private val source = binding.tvSource
        private val publishedAt = binding.tvPublishedAt
        private val image = binding.ivArticleImage

        fun bind(article: Article, listener: OnItemClickListener<Article>) {

            title.text = article.title
            source.text = article.source?.name

            publishedAt.text =
                formatDate(article.publishedAt!!, "yyyy-MM-dd'T'HH:mm:ss'Z'")
            if (article.urlToImage.isNullOrEmpty()) {
                image.setImageResource(R.drawable.ic_no_image)
            } else {
                Glide.with(itemView.context).load(article.urlToImage).into(image)
            }
            itemView.setOnClickListener {
                listener.onClick(article)
            }

            itemView.setOnLongClickListener {
                listener.onLongClick(it, article)
                true
            }

        }
    }

    inner class ShimmerViewHolder(binding: RowItemNewsArticleShimmerBinding) : RecyclerView.ViewHolder(binding.root) {
        val shimmerLayout = binding.shimmerLayout
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_NEWS
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SHIMMER) {
            val binding = RowItemNewsArticleShimmerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ShimmerViewHolder(binding)
        } else {
            val binding =
                RowItemNewsArticleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ArticleViewHolder(binding)
        }

    }

    override fun getItemCount(): Int {
        return if (isLoading) shimmerItemsCount else differ.currentList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_SHIMMER -> {
                val shimmerHolder = holder as ShimmerViewHolder
                shimmerHolder.shimmerLayout.startShimmer()
            }
            VIEW_TYPE_NEWS -> {
                val article = differ.currentList[position]
                val articleHolder = holder as ArticleViewHolder
                articleHolder.bind(article, listener)
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