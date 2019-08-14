package io.taptalk.TapTalk.DiffCallback

import android.support.v7.util.DiffUtil
import io.taptalk.TapTalk.Model.TAPUserModel

class TAPGroupMemberDiffCallback(private val oldList: List<TAPUserModel>,
                                 private val newList: List<TAPUserModel>)
    : DiffUtil.Callback() {

    override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
        return oldList[p0].userID == newList[p1].userID
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
        return oldList[p0].name == newList[p1].name
    }

}