package com.devapps.githubuser.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.githubuser.adapter.UserAdapter
import com.devapps.githubuser.data.remote.response.ItemsItem
import com.devapps.githubuser.databinding.ActivityFavoriteBinding
import com.devapps.githubuser.viewmodel.FavoriteViewModel

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: UserAdapter
    private val favoriteViewModel: FavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Favorite Users"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.layoutManager = layoutManager

        adapter = UserAdapter(object : UserAdapter.OnItemClickListener {
            override fun onItemClick(user: ItemsItem) {
                val intent = Intent(this@FavoriteActivity, DetailUserActivity::class.java)
                intent.putExtra(DetailUserActivity.EXTRA_USER, user.login)
                startActivity(intent)
            }
        })

        binding.rvFavorite.adapter = adapter

        favoriteViewModel.getFavorite()?.observe(this, Observer { favoriteList ->
            val items = favoriteList?.map {
                ItemsItem(login = it.username, avatarUrl = it.avatarUrl)
            } ?: emptyList()

            adapter.submitList(items)
        })


        favoriteViewModel.isLoading.observe(this, this::showLoading)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvFavorite.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvFavorite.visibility = View.VISIBLE
        }
    }
}
