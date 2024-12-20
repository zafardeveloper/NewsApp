package com.example.quicknews.model.article

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Source(
    val id: String? = "",
    val name: String? = ""
): Parcelable