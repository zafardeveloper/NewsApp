package com.example.newsapplication.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsapplication.R
import com.example.newsapplication.model.profile.UserInfoModel
import com.example.newsapplication.util.Constants.AVATAR_IMAGE
import com.example.newsapplication.util.Constants.SHOW_DELETE

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.app_name), Context.MODE_PRIVATE
    )

    private val avatarImageLiveData: MutableLiveData<String?> = MutableLiveData(getAvatarImage())
    private val showDeleteLiveData: MutableLiveData<Int> = MutableLiveData(getShowDelete())
    private val userInfoLiveData: MutableLiveData<UserInfoModel> = MutableLiveData(loadUserInfo())


    fun setAvatarImage(uri: Uri) {
        prefs.edit().putString(AVATAR_IMAGE, uri.toString()).apply()
        avatarImageLiveData.value = uri.toString()
    }

    fun getAvatarImage(): String? {
        return prefs.getString(AVATAR_IMAGE, null)
    }

    fun deleteAvatarImage() {
        prefs.edit().remove(AVATAR_IMAGE).apply()
        avatarImageLiveData.postValue(null)
    }

    fun getAvatarImageLiveData(): LiveData<String?> {
        return avatarImageLiveData
    }

    fun setShowDelete(id: Int) {
        prefs.edit().putInt(SHOW_DELETE, id).apply()
        showDeleteLiveData.postValue(id)
    }

    private fun getShowDelete(): Int {
        return prefs.getInt(SHOW_DELETE, 0)
    }

    fun getShowDeleteLiveData(): LiveData<Int> {
        return showDeleteLiveData
    }

    fun saveUserInfo(userInfo: UserInfoModel) {
        prefs.edit().apply {
            putString("name", userInfo.name)
            putString("username", userInfo.username)
            putString("phone", userInfo.phone)
            putString("gender", userInfo.gender)
            putString("birthday", userInfo.birthday)
            putString("email", userInfo.email)
            apply()
        }
        userInfoLiveData.postValue(userInfo)
    }

    fun loadUserInfo(): UserInfoModel {
        return UserInfoModel(
            name = prefs.getString("name", null) ?: "",
            username = prefs.getString("username", null) ?: "",
            phone = prefs.getString("phone", null) ?: "",
            gender = prefs.getString("gender", null) ?: "",
            birthday = prefs.getString("birthday", null) ?: "",
            email = prefs.getString("email", null) ?: ""
        )
    }

    fun refreshUserInfoLiveData(): LiveData<UserInfoModel> {
        return userInfoLiveData
    }

}
