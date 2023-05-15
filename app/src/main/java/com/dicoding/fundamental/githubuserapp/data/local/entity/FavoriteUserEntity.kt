package com.dicoding.fundamental.githubuserapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "tbl_fav")
@Parcelize
data class FavoriteUserEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "login")
    val username: String,

    @ColumnInfo(name = "avatar_url")
    val avatar: String? = null,

    @ColumnInfo(name= "favorite")
    var isFavorite: Boolean? = null
) : Parcelable