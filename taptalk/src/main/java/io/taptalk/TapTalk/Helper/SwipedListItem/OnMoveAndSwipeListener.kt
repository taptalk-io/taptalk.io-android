package io.taptalk.TapTalk.Helper.SwipedListItem

interface OnMoveAndSwipeListener {
    fun onReadOrUnreadItem(position: Int)

    fun onMuteOrUnmuteItem(position: Int)

    fun onPinOrUnpinItem(position: Int)
}
