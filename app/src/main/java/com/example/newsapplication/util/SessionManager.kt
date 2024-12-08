package com.example.newsapplication.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newsapplication.R
import com.example.newsapplication.util.Constants.AVATAR_IMAGE
import com.example.newsapplication.util.Constants.SHOW_DELETE

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.app_name), Context.MODE_PRIVATE
    )

    private val avatarImageLiveData: MutableLiveData<String?> = MutableLiveData(getAvatarImage())
    private val showDeleteLiveData: MutableLiveData<Int> = MutableLiveData(getShowDelete())

    fun setAvatarImage(uri: Uri) {
        prefs.edit().putString(AVATAR_IMAGE, uri.toString()).apply()
        avatarImageLiveData.postValue(uri.toString())
    }

    private fun getAvatarImage(): String? {
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


}
