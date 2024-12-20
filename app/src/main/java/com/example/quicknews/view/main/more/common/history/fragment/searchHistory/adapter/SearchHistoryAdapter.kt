package com.example.quicknews.view.main.more.common.history.fragment.searchHistory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.databinding.RowItemSearchHistoryBinding
import com.example.quicknews.db.searchHistory.SearchHistoryEntity

class SearchHistoryAdapter(private val listener: Listener) :
    RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {

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
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchHistory = differ.currentList[position]
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




