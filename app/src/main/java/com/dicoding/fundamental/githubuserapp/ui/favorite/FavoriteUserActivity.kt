package com.dicoding.fundamental.githubuserapp.ui.favorite

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.fundamental.githubuserapp.R
import com.dicoding.fundamental.githubuserapp.databinding.ActivityFavoriteUserBinding
import com.dicoding.fundamental.githubuserapp.utils.ViewModelFactory

class FavoriteUserActivity : AppCompatActivity() {

    private lateinit var favoriteUserBinding: ActivityFavoriteUserBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val favoriteViewModel: FavoriteViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        favoriteUserBinding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(favoriteUserBinding.root)

        supportActionBar?.apply {
            title = getString(R.string.title_favorite)
            setDisplayHomeAsUpEnabled(true)
        }

        factory = ViewModelFactory.getInstance(this)

        favoriteAdapter = FavoriteAdapter()
        showRecyclerList()
        favoriteViewModel.getFavoritedUser().observe(this) { favList ->
            favoriteUserBinding.pbFav.visibility = View.GONE
            favoriteAdapter.updateUserList(favList)
        }
    }

    private fun showRecyclerList() {
        favoriteUserBinding.rvFav.apply {
            layoutManager = LinearLayoutManager(this@FavoriteUserActivity)
            setHasFixedSize(true)
            adapter = favoriteAdapter
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}