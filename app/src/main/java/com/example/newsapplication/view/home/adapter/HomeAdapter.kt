package com.example.newsapplication.view.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.newsapplication.R
import com.example.newsapplication.databinding.RowItemNewsHomeBinding
import com.example.newsapplication.model.Article
import com.example.newsapplication.util.formatDate
import com.squareup.picasso.Picasso

class HomeAdapter(private val listener: Listener) :
    RecyclerView.Adapter<HomeAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(binding: RowItemNewsHomeBinding) :
        ViewHolder(binding.root) {
        private val title = binding.tvTitle
//        private val description = binding.tvDescription
        private val source = binding.tvSource
        private val publishedAt = binding.tvPublishedAt
        private val image = binding.ivArticleImage
        fun bind(article: Article) {
            title.text = article.title
//            description.text = article.description
            source.text = article.source?.name

            publishedAt.text = formatDate(article.publishedAt!!, "yyyy-MM-dd'T'HH:mm:ss'Z'", "dd MMMM")
            if (article.urlToImage == null) {
                image.setImageResource(R.drawable.ic_no_image)
            } else {
                Picasso.get().load(article.urlToImage).into(image)
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
            RowItemNewsHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)

        holder.itemView.setOnClickListener {
            listener.onClick(article)
        }
    }

    interface Listener {
        fun onClick(item: Article)
    }
}