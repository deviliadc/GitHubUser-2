package com.devapps.githubuser.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devapps.githubuser.data.remote.response.ItemsItem
import com.devapps.githubuser.databinding.FragmentFollowingBinding
import com.devapps.githubuser.viewmodel.DetailViewModel
import com.devapps.githubuser.adapter.UserAdapter

class FollowingFragment : Fragment() {
    private lateinit var binding: FragmentFollowingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFollowingBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(context)
        binding.rvFollowing.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        binding.rvFollowing.addItemDecoration(itemDecoration)

        val detailViewModel = ViewModelProvider(requireActivity())[DetailViewModel::class.java]

        val adapter = UserAdapter(object : UserAdapter.OnItemClickListener {
            override fun onItemClick(user: ItemsItem) {
                onUserItemClicked(user)
            }
        })

        binding.rvFollowing.adapter = adapter

        detailViewModel.allfollowings.observe(viewLifecycleOwner) { followingsData ->
            showFollowings(followingsData, adapter)
        }

        detailViewModel.isLoadingFollowing.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        return binding.root
    }

    private fun showFollowings(dataUsers: List<ItemsItem>, adapter: UserAdapter) {
        adapter.submitList(dataUsers)
    }

    private fun onUserItemClicked(data: ItemsItem) {
        val intent = Intent(activity, DetailUserActivity::class.java)
        intent.putExtra(DetailUserActivity.EXTRA_USER, data.login)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarFollowing.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
