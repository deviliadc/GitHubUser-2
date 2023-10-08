package com.devapps.githubuser.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devapps.githubuser.data.remote.response.ItemsItem
import com.devapps.githubuser.data.remote.response.UserResponse
import com.devapps.githubuser.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application){

    private val _listUser = MutableLiveData<List<ItemsItem>?>()
    val listUser: LiveData<List<ItemsItem>?> = _listUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    companion object {
        private const val TAG = "MainViewModel"
        private const val USER_ID = "d"
    }

    init {
        getData(USER_ID)
    }


    fun getData(userData: String) {
        _isLoading.value = true
        val service = ApiConfig.getApiService().searchUsers(userData)
        service.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listUser.value = response.body()?.items
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                _error.postValue("onFailure: ${t.message.toString()}")
            }
        })
    }
}
