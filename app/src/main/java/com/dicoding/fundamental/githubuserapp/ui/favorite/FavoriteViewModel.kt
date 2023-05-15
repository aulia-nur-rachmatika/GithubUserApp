package com.dicoding.fundamental.githubuserapp.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.fundamental.githubuserapp.data.source.FavoriteUserRepository
import com.dicoding.fundamental.githubuserapp.data.local.entity.FavoriteUserEntity
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteUserRepository) : ViewModel() {

    fun getFavoritedUser() = repository.getFavoritedUser()

    fun saveDeleteUser(user: FavoriteUserEntity, isFavorited: Boolean) {
        viewModelScope.launch {
            if (isFavorited) {
                repository.deleteFavoriteUser(user, false)
            } else {
                repository.addFavoriteUser(user, true)
            }
        }
    }
}