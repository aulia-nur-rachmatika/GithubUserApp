package com.dicoding.fundamental.githubuserapp.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.dicoding.fundamental.githubuserapp.data.local.remote.retrofit.ApiService
import com.dicoding.fundamental.githubuserapp.data.local.entity.FavoriteUserEntity
import com.dicoding.fundamental.githubuserapp.data.room.UserDao
import com.dicoding.fundamental.githubuserapp.utils.Event
import com.dicoding.fundamental.githubuserapp.utils.SettingPreferences
import com.dicoding.fundamental.githubuserapp.data.local.remote.response.GithubSearchResponse
import com.dicoding.fundamental.githubuserapp.data.local.remote.response.GithubUser
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteUserRepository private constructor(
    private val preferences: SettingPreferences,
    private val apiService: ApiService,
    private val userDao: UserDao
) {

    private val _listGithubUser = MutableLiveData<List<GithubUser>?>()
    val listGithubUser: MutableLiveData<List<GithubUser>?> = _listGithubUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun getUser(query: String?) {
        _isLoading.value = true
        val client = apiService.getUser(query)
        client.enqueue(object : Callback<GithubSearchResponse> {
            override fun onResponse(
                call: Call<GithubSearchResponse>,
                response: Response<GithubSearchResponse>
            ) {
                _isLoading.value = false
                val listUser = response.body()?.items
                if (response.isSuccessful) {
                    if (listUser.isNullOrEmpty()) {
                        _toastText.value = Event("User not found")
                    } else {
                        _listGithubUser.value = listUser
                    }
                } else {
                    _toastText.value = Event(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GithubSearchResponse>, t: Throwable) {
                _isLoading.value = false
                _toastText.value = Event("No internet connection")
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getThemeSetting(): Flow<Boolean> = preferences.getThemeSetting()

    suspend fun saveThemeSetting(isNightModeActive: Boolean) {
        preferences.saveThemeSetting(isNightModeActive)
    }

    fun getFavoritedUser(): LiveData<List<FavoriteUserEntity>> {
        return userDao.getFavoritedUser().asLiveData()
    }

    suspend fun addFavoriteUser(user: FavoriteUserEntity, favoriteState: Boolean) {
        user.isFavorite = favoriteState
        userDao.insertUser(user)
    }

    suspend fun deleteFavoriteUser(user: FavoriteUserEntity, favoriteState: Boolean) {
        user.isFavorite = favoriteState
        userDao.deleteUser(user)
    }

    companion object {
        private const val TAG = "UserRepository"

        @Volatile
        private var instance: FavoriteUserRepository? = null
        fun getInstance(
            preferences: SettingPreferences,
            apiService: ApiService,
            userDao: UserDao,
        ): FavoriteUserRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteUserRepository(preferences, apiService, userDao)
            }.also { instance = it }
    }
}
