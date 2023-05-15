package com.dicoding.fundamental.githubuserapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.fundamental.githubuserapp.data.local.entity.FavoriteUserEntity

class UserDiffUtils(private val oldList: List<FavoriteUserEntity>, private val newList: List<FavoriteUserEntity>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList == newList

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition].username
        val latest = newList[newItemPosition].username
        return old == latest
    }
}