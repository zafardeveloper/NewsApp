package com.example.newsapplication.common

import android.view.View

interface OnItemClickListener<T> {
    fun onClick(item: T)

    fun onLongClick(view: View, item: T)
}