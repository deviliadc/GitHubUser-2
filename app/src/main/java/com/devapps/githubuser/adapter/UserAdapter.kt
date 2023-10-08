package com.devapps.githubuser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devapps.githubuser.R
import com.devapps.githubuser.data.remote.response.ItemsItem

class UserAdapter(private var onItemClickListener: OnItemClickListener? = null) :
    ListAdapter<ItemsItem, UserAdapter.ViewHolder>(DIFF_CALLBACK) {

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val imgProfile: ImageView = itemView.findViewById(R.id.imgProfile)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = getItem(position)
                    onItemClickListener?.onItemClick(user)
                }
            }
        }

        fun bind(user: ItemsItem) {
            tvUsername.text = user.login

            if (!user.avatarUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(user.avatarUrl)
                    .centerCrop()
                    .into(imgProfile)
            } else {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_account_circle)
                    .into(imgProfile)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(user: ItemsItem)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemsItem>() {
            override fun areItemsTheSame(oldItem: ItemsItem, newItem: ItemsItem): Boolean {
                return oldItem.login == newItem.login
            }

            override fun areContentsTheSame(oldItem: ItemsItem, newItem: ItemsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
