package com.example.quicknews.model.profile

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfoModel(
    val name: String,
    val username: String,
    val phone: String,
    val gender: String,
    val birthday: String,
    val email: String
): Parcelable
