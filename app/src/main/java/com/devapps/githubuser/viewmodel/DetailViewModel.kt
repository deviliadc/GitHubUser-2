package com.devapps.githubuser.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.devapps.githubuser.data.local.entity.UserFavEntity
import com.devapps.githubuser.data.local.room.UserFavDao
import com.devapps.githubuser.data.local.room.UserFavRoomDatabase
import com.devapps.githubuser.data.remote.response.DetailUserResponse
import com.devapps.githubuser.data.remote.response.ItemsItem
import com.devapps.githubuser.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _detailuser = MutableLiveData<DetailUserResponse>()
    val detailuser: LiveData<DetailUserResponse> = _detailuser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _followViewModel = FollowViewModel(application)

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> get() = _isFavorite

    private val favoriteDao: UserFavDao?
    private val favoriteDatabase: UserFavRoomDatabase

    init {
        favoriteDatabase = UserFavRoomDatabase.getDatabase(application)
        favoriteDao = favoriteDatabase.userFavDao()
    }

    private var userlogin: String = ""

    fun setUserLogin(userLogin: String) {
        userlogin = userLogin
        getDetailUser()
        _followViewModel.getFollowers(ApiConfig.getApiService().getUserFollowers(userlogin))
        _followViewModel.getFollowing(ApiConfig.getApiService().getUserFollowing(userlogin))
    }

    private fun getDetailUser() {
        _isLoading.value = true
        val service = ApiConfig.getApiService().getUserDetail(userlogin)
        service.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _detailuser.value = responseBody!!

                    viewModelScope.launch(Dispatchers.IO) {
                        val isFavorite = checkFavorite(userlogin)
                        _isFavorite.postValue(isFavorite)
                    }

                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    val isLoadingFollower: LiveData<Boolean> = _followViewModel.isLoadingFollowers
    val allfollowers: LiveData<List<ItemsItem>> = _followViewModel.followers

    val isLoadingFollowing: LiveData<Boolean> = _followViewModel.isLoadingFollowing
    val allfollowings: LiveData<List<ItemsItem>> = _followViewModel.following

    fun toggleFavorite() {
        val isCurrentlyFavorite = isFavorite.value ?: false
        val userFavEntity = UserFavEntity(username = userlogin, avatarUrl = detailuser.value?.avatarUrl)

        viewModelScope.launch(Dispatchers.IO) {
            if (!isCurrentlyFavorite && !checkFavorite(userlogin)) {
                addToFavorites(userFavEntity)
                showToastOnMain("Added to Favorites")
            } else if (isCurrentlyFavorite) {
                removeFromFavorites(userlogin)
                showToastOnMain("Removed from Favorites")
            }
        }
    }

    private fun showToastOnMain(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFromFavorites(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isFavorite.postValue(false)
            favoriteDao?.deleteByUsername(username)
        }
    }

    suspend fun checkFavorite(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            favoriteDao?.isFavorite(username) ?: false
        }
    }

    private fun addToFavorites(userFavEntity: UserFavEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            _isFavorite.postValue(true)
            favoriteDao?.insert(userFavEntity)
        }
    }

    companion object {
        private const val TAG = "DetailViewModel"
    }
}
