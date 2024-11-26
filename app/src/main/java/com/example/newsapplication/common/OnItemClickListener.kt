package com.example.newsapplication.common

interface OnItemClickListener<T> {
    fun onClick(item: T)
}