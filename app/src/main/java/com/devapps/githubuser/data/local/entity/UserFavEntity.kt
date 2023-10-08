package com.devapps.githubuser.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity("userfaventity")
@Parcelize
data class UserFavEntity(
    @PrimaryKey
    @ColumnInfo(name = "username")
    var username: String = "",

    @ColumnInfo(name = "avatar")
    val avatarUrl: String? = null
): Parcelable