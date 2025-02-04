package com.example.quicknews.view.main.categories.tabFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.databinding.RowItemNewsCategoriesHorizontalBinding
import com.example.quicknews.model.article.Article
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.util.Util.Companion.formatDate

class TabChildAdapter(
    private val articleList: List<Article>,
    private val listener: OnItemClickListener<Article>
) :
    RecyclerView.Adapter<TabChildAdapter.ChildViewHolder>() {

    inner class ChildViewHolder(binding: RowItemNewsCategoriesHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
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
                Glide.with(itemView.context).load(article.urlToImage)
                    .error(R.drawable.ic_no_image)
                    .into(image)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val binding = RowItemNewsCategoriesHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChildViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (articleList.size > 10) articleList.take(10).size else articleList.size
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val article = articleList[position]
        holder.bind(article)
    }
}
