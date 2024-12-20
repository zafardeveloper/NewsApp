package com.example.quicknews.model.setting

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingLayoutModel(
    val icon: Int,
    val title: String
) : Parcelable
