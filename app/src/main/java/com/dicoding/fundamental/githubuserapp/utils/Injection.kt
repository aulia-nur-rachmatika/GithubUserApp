package com.dicoding.fundamental.githubuserapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.fundamental.githubuserapp.data.source.FavoriteUserRepository
import com.dicoding.fundamental.githubuserapp.data.local.remote.retrofit.ApiConfig
import com.dicoding.fundamental.githubuserapp.data.room.UserDatabase

private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
    "settings"
)

object Injection {
    fun provideRepository(context: Context): FavoriteUserRepository {
        val apiService = ApiConfig.getApiService()
        val database = UserDatabase.getInstance(context)
        val dao = database.userDao()
        val preferences = SettingPreferences.getInstance(context.dataStore)
        return FavoriteUserRepository.getInstance(preferences, apiService, dao)
    }
}