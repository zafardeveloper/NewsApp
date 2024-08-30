package com.example.newsapplication.view.search.historyAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.databinding.RowItemSearchHistoryBinding
import com.example.newsapplication.db.searchHistory.SearchHistoryEntity

class SearchHistoryAdapter(private val listener: Listener) : RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RowItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val searchText = binding.searchText

        fun bind(searchHistory: SearchHistoryEntity) {
            searchText.text = searchHistory.searchQuery
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<SearchHistoryEntity>() {
        override fun areItemsTheSame(
            oldItem: SearchHistoryEntity,
            newItem: SearchHistoryEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SearchHistoryEntity,
            newItem: SearchHistoryEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.distinct().size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchHistory = differ.currentList.distinct()[position]
        holder.bind(searchHistory)
        holder.itemView.setOnClickListener {
            listener.onClickHistory(searchHistory)
        }
        holder.itemView.setOnLongClickListener {
            listener.onLongClickHistory(searchHistory)
            true
        }
    }

    interface Listener {
        fun onClickHistory(searchHistory: SearchHistoryEntity)
        fun onLongClickHistory(searchHistory: SearchHistoryEntity)
    }

}