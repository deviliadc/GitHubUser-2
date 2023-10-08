package com.devapps.githubuser.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devapps.githubuser.data.local.entity.UserFavEntity
import com.devapps.githubuser.data.local.room.UserFavDao
import com.devapps.githubuser.data.local.room.UserFavRoomDatabase

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val favDao: UserFavDao?
    private val userFavDatabase: UserFavRoomDatabase?
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        userFavDatabase = UserFavRoomDatabase.getDatabase(application)
        favDao = userFavDatabase.userFavDao()
    }

    fun getFavorite(): LiveData<List<UserFavEntity>>? {
        return favDao?.getAllUser()
    }
}