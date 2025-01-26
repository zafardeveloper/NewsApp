package com.example.quicknews.view.main.more.common.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.databinding.RowItemNewsArticleBinding
import com.example.quicknews.db.article.history.HistoryEntity
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.util.Util.Companion.formatDate

class HistoryAdapter(private val listener: OnItemClickListener<HistoryEntity>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RowItemNewsArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvTitle
        private val source = binding.tvSource
        private val publishedAt = binding.tvPublishedAt
        private val image = binding.ivArticleImage
        val constraintLayout = binding.constraintLayout

        fun bind(historyEntity: HistoryEntity) {

            title.text = historyEntity.title
            source.text = historyEntity.source?.name

            publishedAt.text =
                formatDate(historyEntity.publishedAt!!, "yyyy-MM-dd'T'HH:mm:ss'Z'")
            if (historyEntity.urlToImage.isNullOrEmpty()) {
                image.setImageResource(R.drawable.ic_no_image)
            } else {
                Glide.with(itemView.context).load(historyEntity.urlToImage).into(image)
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<HistoryEntity>() {
        override fun areItemsTheSame(
            oldItem: HistoryEntity,
            newItem: HistoryEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: HistoryEntity,
            newItem: HistoryEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowItemNewsArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.distinct().size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyEntity = differ.currentList.distinct()[position]
        holder.bind(historyEntity)
        holder.constraintLayout.startAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.emergence
            )
        )
        holder.itemView.setOnClickListener {
            listener.onClick(historyEntity)
        }
    }
}