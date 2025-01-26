package com.example.quicknews.view.headlines

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.databinding.RowItemNewsArticleBinding
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.util.Util.Companion.formatDate

class HeadlinesAdapter(private val listener: OnItemClickListener<Article>) :
    PagingDataAdapter<Article, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }) {

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
        val binding = RowItemNewsArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val article = getItem(position)
        if (article != null) {
            (holder as ArticleViewHolder).bind(article, listener)
        }
    }
}
