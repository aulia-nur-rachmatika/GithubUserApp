package com.dicoding.fundamental.githubuserapp.ui.detail

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.fundamental.githubuserapp.R
import com.dicoding.fundamental.githubuserapp.data.local.entity.FavoriteUserEntity
import com.dicoding.fundamental.githubuserapp.databinding.ActivityDetailBinding
import com.dicoding.fundamental.githubuserapp.utils.ViewModelFactory
import com.dicoding.fundamental.githubuserapp.ui.favorite.FavoriteViewModel
import com.dicoding.fundamental.githubuserapp.ui.follow.FollowFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {

    private lateinit var detailBinding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel>()
    private lateinit var factory: ViewModelFactory
    private val favoriteViewModel: FavoriteViewModel by viewModels { factory }
    private var username: String? = null
    private var avatar: String? = null

    // Definisikan ID channel notifikasi
    private val CHANNEL_ID = "my_channel"
    // Definisikan ID unik untuk notifikasi
    private val NOTIFICATION_ID = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)

        supportActionBar?.apply {
            title = getString(R.string.title_detail)
            setDisplayHomeAsUpEnabled(true)
        }

        val username = intent.getStringExtra(EXTRA_USER)
        detailViewModel.getDetailUser(username)

        detailViewModel.userData.observe(this) { userData ->
            detailBinding.apply {
                tvUsername.text = userData.username
                tvName.text = userData?.name ?: "-"

                val shortFollowers = userData.followers
                if (shortFollowers > 10000) {
                    "${shortFollowers / 1000}.${(shortFollowers % 1000) / 100}K".also {
                        tvFollowers.text = it
                    }
                } else {
                    tvFollowers.text = userData.followers.toString()
                }

                val shortFollowing = userData.following
                if (shortFollowing > 10000) {
                    "${shortFollowing / 1000}.${(shortFollowing % 1000) / 100}K".also {
                        tvFollowing.text = it
                    }
                } else {
                    tvFollowing.text = userData.following.toString()
                }


                Glide.with(this@DetailActivity)
                    .load(userData.avatar)
                    .apply(
                        RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    )
                    .into(ivAvatar)

                val fragment = mutableListOf<Fragment>(
                    FollowFragment.newInstance(FollowFragment.FOLLOWING),
                    FollowFragment.newInstance(FollowFragment.FOLLOWERS)


                )

                val fragmentTitle = mutableListOf(
                    getString(R.string.following),
                    getString(R.string.followers)


                )

                val detailAdapter = DetailAdapter(this@DetailActivity, fragment)
                viewPager.adapter = detailAdapter

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = fragmentTitle[position]
                }.attach()

                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        if (tab?.position == FollowFragment.FOLLOWING) {
                            detailViewModel.getFollowing(userData.username)
                        } else {
                            detailViewModel.getFollowers(userData.username)
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })
                detailViewModel.getFollowing(userData.username)
            }
            this.username = userData.username.toString()
            this.avatar = userData.avatar.toString()
        }

        detailViewModel.toastText.observe(this) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        factory = ViewModelFactory.getInstance(this)

        favoriteViewModel.getFavoritedUser().observe(this) { favList ->
            val isFavorite = favList.any {
                it.username == username
            }
            setIconFavorite(isFavorite)

            detailBinding.fabFav.setOnClickListener {
                val entity = username?.let { FavoriteUserEntity(it, avatar, false) }

                try {
                    if (entity != null) favoriteViewModel.saveDeleteUser(entity, favList.any {
                        it.username == username
                    })
                } catch (e: Exception) {
                    Toast.makeText(
                        this, e.toString(), Toast.LENGTH_SHORT
                    ).show()
                }

                if (isFavorite) {
                    Toast.makeText(
                        this, "Remove $username from favorite", Toast.LENGTH_SHORT
                    ).show()
                    setIconFavorite(isFavorite)
                    removenotif()
                } else {
                    Toast.makeText(
                        this, "Add $username to favorite", Toast.LENGTH_SHORT
                    ).show()
                    setIconFavorite(isFavorite)

              addnotif()
                }

            }
        }
    }
    private fun addnotif() {
        // Buat intent untuk menampilkan notifikasi
        val notificationIntent = Intent(this, DetailActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Notification Channel"
            val descriptionText = "$username has been added to favorite"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register channel dengan notification manager
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Buat builder untuk notifikasi
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle("My Notification")
            .setContentText("$username added to favorite")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        // Tampilkan notifikasi
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun removenotif() {
        // Buat intent untuk menampilkan notifikasi
        val notificationIntent = Intent(this, DetailActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Notification Channel"
            val descriptionText = "$username has been removed from favorite"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register channel dengan notification manager
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Buat builder untuk notifikasi
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle("My Notification")
            .setContentText("$username has been removed from favorite")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        // Tampilkan notifikasi
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }


    private fun showLoading(isLoading: Boolean) {
        detailBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setIconFavorite(isFavorited: Boolean) {
        detailBinding.fabFav.apply {
            if (isFavorited) {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.ic_favorited
                    )
                )
            } else {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DetailActivity,
                        R.drawable.ic_favorite
                    )
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"
    }
}