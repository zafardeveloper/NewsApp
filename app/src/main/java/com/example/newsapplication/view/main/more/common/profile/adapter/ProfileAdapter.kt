package com.example.newsapplication.view.main.more.common.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication.databinding.RowTemLayoutEdittextsProfileBinding
import com.example.newsapplication.model.profile.ProfileInfoModel

class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    inner class ViewHolder(binding: RowTemLayoutEdittextsProfileBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.titleTV
        val content = binding.contentET

        fun bind(profileInfoModel: ProfileInfoModel) {
            title.text = profileInfoModel.title
            content.setText(profileInfoModel.content)
        }

    }

    private val differCallBack = object : DiffUtil.ItemCallback<ProfileInfoModel>() {
        override fun areItemsTheSame(oldItem: ProfileInfoModel, newItem: ProfileInfoModel): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: ProfileInfoModel, newItem: ProfileInfoModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowTemLayoutEdittextsProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profileInfoModel = differ.currentList[position]
        holder.bind(profileInfoModel)

    }
}