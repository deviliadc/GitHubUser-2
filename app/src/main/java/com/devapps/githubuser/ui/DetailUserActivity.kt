package com.devapps.githubuser.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.devapps.githubuser.R
import com.devapps.githubuser.databinding.ActivityDetailUserBinding
import com.devapps.githubuser.adapter.SectionsPagerAdapter
import com.devapps.githubuser.data.remote.response.DetailUserResponse
import com.devapps.githubuser.viewmodel.DetailViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var detailViewModel: DetailViewModel

    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = intent.getStringExtra(EXTRA_USER) ?: ""

        supportActionBar?.elevation = 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        detailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        user.let {
            detailViewModel.setUserLogin(it)
        }

        detailViewModel.detailuser.observe(this) { detailUserResponse: DetailUserResponse? ->
            if (detailUserResponse != null) {
                showLoading(false)
                setDetailUser(detailUserResponse)
            } else {
                showLoading(true)
            }
        }

        setViewPager()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.visibility = View.VISIBLE
        backButton.setOnClickListener {
            onBackPressed()
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)

        val fabFavorite = findViewById<FloatingActionButton>(R.id.fabFavorite)
        fabFavorite.setOnClickListener {
            detailViewModel.toggleFavorite()
        }

        detailViewModel.isFavorite.observe(this) { isFavorite ->
            updateFavoriteButtonState(isFavorite)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val isFavorite = detailViewModel.checkFavorite(EXTRA_USER)
            withContext(Dispatchers.Main) {
                updateFavoriteButtonState(isFavorite)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setViewPager() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            resetDetailUser()
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setDetailUser(detailUser: DetailUserResponse) {
        binding.toolbarUsername.text = detailUser.login
        binding.tvName.text = detailUser.name
        binding.tvUsername.text = detailUser.login
        binding.tvFollowersCount.text = detailUser.followers.toString()
        binding.tvFollowingCount.text = detailUser.following.toString()
        binding.tvRepositoryCount.text = detailUser.publicRepos.toString()

        supportActionBar?.title = detailUser.login.toUsernameFormat()

        Glide.with(this)
            .load(detailUser.avatarUrl)
            .placeholder(R.drawable.ic_account_circle)
            .into(binding.ivProfile)

        val isFavorite = detailViewModel.isFavorite.value ?: false
        updateFavoriteButtonState(isFavorite)
    }

    private fun resetDetailUser() {
        binding.tvName.text = ""
        binding.tvUsername.text = ""
        binding.tvFollowersCount.text = ""
        binding.tvFollowingCount.text = ""
        binding.tvRepositoryCount.text = ""

        val actionBar = supportActionBar
        actionBar?.title = ""
        Glide.with(this)
            .load(R.drawable.ic_account_circle)
            .placeholder(R.drawable.ic_account_circle)
            .into(binding.ivProfile)
    }

    private fun String.toUsernameFormat(): String {
        return "@$this"
    }

    private fun updateFavoriteButtonState(isFavorite: Boolean) {
        val fabFavorite = findViewById<FloatingActionButton>(R.id.fabFavorite)
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
        this.isFavorite = isFavorite
    }

    companion object {
        const val EXTRA_USER = "extra_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )
    }
}
