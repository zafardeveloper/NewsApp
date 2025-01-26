package com.example.quicknews.view.main.more.common.history.fragment.searchHistory.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.quicknews.R
import com.example.quicknews.databinding.RowItemDateHeaderBinding
import com.example.quicknews.databinding.RowItemSearchHistoryAllBinding
import com.example.quicknews.db.searchHistory.SearchHistoryEntity
import com.example.quicknews.db.searchHistory.SearchHistoryItem
import com.example.quicknews.util.Util.Companion.getFormattedTime

class SearchHistoryAdapter(
    private val listener: Listener,
    private val showDeleteMenu: (Boolean) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {

    var isSelectionEnable = false
    val itemSelectedList = mutableListOf<Int>()
    val itemSelectedPositions = mutableListOf<Int>()

    companion object {
        private const val VIEW_TYPE_DATE = 0
        private const val VIEW_TYPE_HISTORY = 1
    }

    inner class HistoryViewHolder(binding: RowItemSearchHistoryAllBinding) :
        ViewHolder(binding.root) {
        private val searchTextTV = binding.searchTextTV
        private val searchTimeTV = binding.searchTimeTV

        fun bind(searchHistory: SearchHistoryEntity) {
            searchTextTV.text = searchHistory.searchQuery
            searchTimeTV.text = getFormattedTime(searchHistory.timeStamp)
        }
    }

    inner class DateViewHolder(binding: RowItemDateHeaderBinding) :
        ViewHolder(binding.root) {
        private val date = binding.dateText
        fun bind(item: SearchHistoryItem.DateHeader) {
            date.text = item.date
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<SearchHistoryItem>() {
        override fun areItemsTheSame(
            oldItem: SearchHistoryItem,
            newItem: SearchHistoryItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SearchHistoryItem,
            newItem: SearchHistoryItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is SearchHistoryItem.DateHeader -> VIEW_TYPE_DATE
            is SearchHistoryItem.History -> VIEW_TYPE_HISTORY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE -> {
                val binding = RowItemDateHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                DateViewHolder(binding)
            }

            VIEW_TYPE_HISTORY -> {
                val binding = RowItemSearchHistoryAllBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                HistoryViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = differ.currentList[position]) {
            is SearchHistoryItem.DateHeader -> {
                (holder as DateViewHolder).bind(item)
            }

            is SearchHistoryItem.History -> {
                (holder as HistoryViewHolder).bind(item.entity)

                holder.apply {

                    if (item.entity.isSelected) {
                        updateSelectionState(holder, true)
                    } else {
                        updateSelectionState(holder, false)
                    }

                    itemView.setOnClickListener {
                        if (itemSelectedPositions.contains(position)) {
                            deselectItem(holder, item, position)
                        } else if (isSelectionEnable) {
                            selectItem(holder, item, position)
                        } else {
                            listener.onClickHistory(item.entity)
                        }
                    }

                    itemView.setOnLongClickListener {
                        if (!isSelectionEnable) {
                            isSelectionEnable = true
                            showDeleteMenu(true)
                        }
                        if (itemSelectedPositions.contains(position)) {
                            deselectItem(holder, item, position)
                        } else selectItem(holder, item, position)

                        true
                    }
                }
            }
        }
    }


    private fun selectItem(
        holder: HistoryViewHolder,
        item: SearchHistoryItem.History,
        position: Int
    ) {
        isSelectionEnable = true
        if (!itemSelectedList.contains(item.entity.id)) {
            itemSelectedPositions.add(position)
            itemSelectedList.add(item.entity.id)
            listener.updateCounterListener(itemSelectedList.size)
            item.entity.isSelected = true
        }
        updateSelectionState(holder, true)
        showDeleteMenu(true)
    }

    private fun deselectItem(
        holder: HistoryViewHolder,
        item: SearchHistoryItem.History,
        position: Int
    ) {
        itemSelectedPositions.remove(position)
        itemSelectedList.remove(item.entity.id)
        item.entity.isSelected = false

        updateSelectionState(holder, false)

        listener.updateCounterListener(itemSelectedList.size)
        if (itemSelectedPositions.isEmpty()) {
            isSelectionEnable = false
            showDeleteMenu(false)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deselectAll() {
        itemSelectedPositions.clear()
        itemSelectedList.clear()
        listener.updateCounterListener(itemSelectedList.size)
        for (item in differ.currentList) {
            if (item is SearchHistoryItem.History) {
                item.entity.isSelected = false
            }
        }
        notifyDataSetChanged()

        isSelectionEnable = false
        showDeleteMenu(false)
    }

    private fun updateSelectionState(
        holder: HistoryViewHolder,
        isSelected: Boolean
    ) {
        holder.itemView.findViewById<ImageView>(R.id.selected).visibility =
            if (isSelected) View.VISIBLE else View.GONE
        holder.itemView.setBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isSelected) R.color.light_blue else android.R.color.transparent
            )
        )
    }


    interface Listener {
        fun onClickHistory(searchHistory: SearchHistoryEntity)
        fun onLongClickHistory(searchHistory: SearchHistoryEntity)
        fun updateCounterListener(count: Int)
    }
}