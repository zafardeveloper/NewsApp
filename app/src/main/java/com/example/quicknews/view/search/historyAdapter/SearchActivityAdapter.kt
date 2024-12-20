package com.example.quicknews.view.search.historyAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quicknews.databinding.RowItemLayoutManageHistoryBinding
import com.example.quicknews.databinding.RowItemSearchHistoryBinding
import com.example.quicknews.db.searchHistory.SearchHistoryEntity

class SearchActivityAdapter(private val listener: Listener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SEARCH_HISTORY = 0
        private const val VIEW_TYPE_MANAGE_HISTORY = 1
    }

    inner class SearchHistoryViewHolder(binding: RowItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val searchText = binding.searchText

        fun bind(searchHistory: SearchHistoryEntity) {
            searchText.text = searchHistory.searchQuery
        }
    }

    inner class ClearListViewHolder(binding: RowItemLayoutManageHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

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

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            differ.currentList.size -> {
                VIEW_TYPE_MANAGE_HISTORY
            }

            else -> VIEW_TYPE_SEARCH_HISTORY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {

            VIEW_TYPE_SEARCH_HISTORY -> {
                val binding =
                    RowItemSearchHistoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                SearchHistoryViewHolder(binding)
            }

            VIEW_TYPE_MANAGE_HISTORY -> {
                val binding = RowItemLayoutManageHistoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ClearListViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_SEARCH_HISTORY -> {
                val searchHistory = differ.currentList[position]
                val searchHistoryHolder = holder as SearchHistoryViewHolder
                searchHistoryHolder.bind(searchHistory)
                searchHistoryHolder.itemView.setOnClickListener {
                    listener.onClickHistory(searchHistory)
                }
                searchHistoryHolder.itemView.setOnLongClickListener {
                    listener.onLongClickHistory(searchHistory)
                    true
                }
            }

            VIEW_TYPE_MANAGE_HISTORY -> {
                val clearListHolder = holder as ClearListViewHolder
                clearListHolder.itemView.setOnClickListener {
                    listener.onManageHistoryClick()
                }
            }
        }


    }

    interface Listener {
        fun onClickHistory(searchHistory: SearchHistoryEntity)
        fun onLongClickHistory(searchHistory: SearchHistoryEntity)
        fun onManageHistoryClick()
    }

}