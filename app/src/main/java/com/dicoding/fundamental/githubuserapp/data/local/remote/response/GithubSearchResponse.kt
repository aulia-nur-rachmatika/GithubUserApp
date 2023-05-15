package com.dicoding.fundamental.githubuserapp.data.local.remote.response

import com.google.gson.annotations.SerializedName

data class GithubSearchResponse(

    @field:SerializedName("items")
    val items: List<GithubUser>
)

data class GithubUser(

    @field:SerializedName("login")
    val username: String?,

    @field:SerializedName("followers")
    val followers: Int,

    @field:SerializedName("avatar_url")
    val avatar: String?,

    @field:SerializedName("following")
    val following: Int,

    @field:SerializedName("name")
    val name: String?,

)