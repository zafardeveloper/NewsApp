package com.example.quicknews.view.main.search.queryAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.R
import com.example.quicknews.databinding.RowItemNewsSearchBinding
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.Util.Companion.formatDate

class SearchQueryAdapter(private val listener: OnItemClickListener<Article>) :
    RecyclerView.Adapter<SearchQueryAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(binding: RowItemNewsSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
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

    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            RowItemNewsSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)
        holder.constraintLayout.startAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.emergence
            )
        )

        holder.itemView.setOnClickListener {
            listener.onClick(article)
        }

        holder.itemView.setOnLongClickListener {
            listener.onLongClick(it, article)
            true
        }

    }

}