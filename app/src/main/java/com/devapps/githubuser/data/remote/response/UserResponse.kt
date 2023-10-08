package com.devapps.githubuser.data.remote.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("total_count")
	val totalCount: Int,

	@field:SerializedName("incomplete_results")
	val incompleteResults: Boolean,

	@field:SerializedName("items")
	val items: List<ItemsItem>
)

data class ItemsItem(

    @field:SerializedName("login")
    val login: String,

    @field:SerializedName("avatar_url")
    val avatarUrl: String?

):Parcelable {

	constructor(parcel: Parcel) : this(
		parcel.readString().toString(),
		parcel.readString().toString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(login)
		parcel.writeString(avatarUrl)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ItemsItem> {
		override fun createFromParcel(parcel: Parcel): ItemsItem {
			return ItemsItem(parcel)
		}

		override fun newArray(size: Int): Array<ItemsItem?> {
			return arrayOfNulls(size)
		}
	}
}
