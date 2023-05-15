package com.dicoding.fundamental.githubuserapp.ui.main

import androidx.lifecycle.*
import com.dicoding.fundamental.githubuserapp.data.source.FavoriteUserRepository
import com.dicoding.fundamental.githubuserapp.utils.Event
import com.dicoding.fundamental.githubuserapp.data.local.remote.response.GithubUser
import kotlinx.coroutines.launch

class MainViewModel(private val repository: FavoriteUserRepository) : ViewModel() {

    val listGithubUser: MutableLiveData<List<GithubUser>?> = repository.listGithubUser
    val isLoading: LiveData<Boolean> = repository.isLoading
    val toastText: LiveData<Event<String>> = repository.toastText


    fun getUser(query: String) {
        viewModelScope.launch {
            repository.getUser(query)
        }
    }

    fun getThemeSetting(): LiveData<Boolean> = repository.getThemeSetting().asLiveData()

    fun saveThemeSetting(newSetting: Boolean) {
        viewModelScope.launch {
            repository.saveThemeSetting(newSetting)
        }
    }
}