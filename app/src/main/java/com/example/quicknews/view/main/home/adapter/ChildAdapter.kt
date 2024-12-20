package com.example.quicknews.view.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.databinding.RowItemNewsHomeBinding
import com.example.quicknews.databinding.RowItemSeeMoreBinding
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.Util.Companion.formatDate

class ChildAdapter(
    private val articleList: List<Article>,
    private val listener: HomeAdapter.Listener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CONTENT = 0
        private const val VIEW_TYPE_SEE_MORE = 1
    }

    inner class ChildViewHolder(binding: RowItemNewsHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvTitle
        private val source = binding.tvSource
        private val publishedAt = binding.tvPublishedAt
        private val image = binding.ivArticleImage
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
            itemView.setOnClickListener {
                listener.onClick(article)
            }
            itemView.setOnLongClickListener {
                listener.onLongClick(it, article)
                true
            }
        }
    }

    inner class SeeMoreViewHolder(binding: RowItemSeeMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val btnSeeMore = binding.btnSeeMore
        fun bind() {
            btnSeeMore.setOnClickListener {
                listener.onShowMoreClick()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            VIEW_TYPE_CONTENT -> {
                val binding = RowItemNewsHomeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChildViewHolder(binding)
            }

            VIEW_TYPE_SEE_MORE -> {
                val binding = RowItemSeeMoreBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SeeMoreViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun getItemCount(): Int {
        return if (articleList.size == 10) 11 else articleList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_CONTENT -> {
                val article = articleList[position]
                val childHolder = holder as ChildViewHolder
                childHolder.bind(article)
            }

            VIEW_TYPE_SEE_MORE -> {
                val seeMoreHolder = holder as SeeMoreViewHolder
                seeMoreHolder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 10) VIEW_TYPE_SEE_MORE else VIEW_TYPE_CONTENT
    }
}

