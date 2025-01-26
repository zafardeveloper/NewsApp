package com.example.quicknews.view.main.more.common.readLater

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quicknews.R
import com.example.quicknews.databinding.RowItemNewsArticleBinding
import com.example.quicknews.db.article.readLater.ReadLaterEntity
import com.example.quicknews.util.OnItemClickListener
import com.example.quicknews.util.Util.Companion.formatDate

class ReadLaterAdapter(private val listener: OnItemClickListener<ReadLaterEntity>) :
    RecyclerView.Adapter<ReadLaterAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RowItemNewsArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvTitle
        private val source = binding.tvSource
        private val publishedAt = binding.tvPublishedAt
        private val image = binding.ivArticleImage
        val constraintLayout = binding.constraintLayout

        fun bind(readLaterEntity: ReadLaterEntity) {

            title.text = readLaterEntity.title
            source.text = readLaterEntity.source?.name

            publishedAt.text =
                formatDate(readLaterEntity.publishedAt!!, "yyyy-MM-dd'T'HH:mm:ss'Z'")
            if (readLaterEntity.urlToImage.isNullOrEmpty()) {
                image.setImageResource(R.drawable.ic_no_image)
            } else {
                Glide.with(itemView.context).load(readLaterEntity.urlToImage).into(image)
            }


            itemView.setOnClickListener {
                listener.onClick(readLaterEntity)
            }

        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<ReadLaterEntity>() {
        override fun areItemsTheSame(
            oldItem: ReadLaterEntity,
            newItem: ReadLaterEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ReadLaterEntity,
            newItem: ReadLaterEntity
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
        val articleEntity = differ.currentList.distinct()[position]
        holder.bind(articleEntity)
        holder.constraintLayout.startAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.emergence
            )
        )
    }
}