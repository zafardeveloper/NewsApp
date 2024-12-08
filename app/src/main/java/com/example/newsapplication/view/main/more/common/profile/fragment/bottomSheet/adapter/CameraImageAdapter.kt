package com.example.newsapplication.view.main.more.common.profile.fragment.bottomSheet.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.R
import com.example.newsapplication.databinding.RowItemLayoutCameraImageBinding
import com.example.newsapplication.model.setting.SettingLayoutModel


class CameraImageAdapter(private val listener: Listener) : RecyclerView.Adapter<CameraImageAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RowItemLayoutCameraImageBinding) : RecyclerView.ViewHolder(binding.root) {
        val icon = binding.icon
        val title = binding.title

        fun bind(settingLayoutModel: SettingLayoutModel) {
            icon.setImageResource(settingLayoutModel.icon)
            title.text = settingLayoutModel.title

            if (settingLayoutModel.icon == R.drawable.ic_trash) {
                title.setTextColor(Color.RED)
            } else {
                title.setTextColor(Color.BLACK)
            }
        }

    }

    private val differCallBack = object : DiffUtil.ItemCallback<SettingLayoutModel>() {
        override fun areItemsTheSame(oldItem: SettingLayoutModel, newItem: SettingLayoutModel): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: SettingLayoutModel, newItem: SettingLayoutModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowItemLayoutCameraImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val settingLayoutModel = differ.currentList[position]
        holder.bind(settingLayoutModel)
        holder.itemView.setOnClickListener {
            listener.onClick(settingLayoutModel)
        }
    }

    interface Listener {
        fun onClick(item: SettingLayoutModel)
    }
}