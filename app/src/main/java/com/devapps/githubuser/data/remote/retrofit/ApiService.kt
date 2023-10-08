package com.devapps.githubuser.data.remote.retrofit

import com.devapps.githubuser.data.remote.response.DetailUserResponse
import com.devapps.githubuser.data.remote.response.ItemsItem
import com.devapps.githubuser.data.remote.response.UserResponse
import com.devapps.githubuser.BuildConfig
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("search/users")
    @Headers("Authorization: ${BuildConfig.KEY}")
    fun searchUsers(
        @Query("q") query: String
    ): Call<UserResponse>

    @GET("users/{username}")
    @Headers("Authorization: ${BuildConfig.KEY}")
    fun getUserDetail(
        @Path("username") username: String
    ): Call<DetailUserResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: ${BuildConfig.KEY}")
    fun getUserFollowers(
        @Path("username") username: String
    ): Call<List<ItemsItem>>

    @GET("users/{username}/following")
    @Headers("Authorization: ${BuildConfig.KEY}")
    fun getUserFollowing(
        @Path("username") username: String
    ): Call<List<ItemsItem>>

}
