package io.moselo.SampleApps.Adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.moselo.SampleApps.listener.TAPShareOptionsInterface
import io.taptalk.TapTalk.Helper.CircleImageView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Model.TAPRoomListModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter

class TAPShareOptionsSelectedAdapter(list: List<TAPRoomListModel>, private val listener: TAPShareOptionsInterface): TAPBaseAdapter<TAPRoomListModel, TAPBaseViewHolder<TAPRoomListModel>>() {

    init {
        items = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TAPRoomListModel> {
        return SelectedContactsViewHolder(parent, R.layout.tap_cell_group_member)
    }

    inner class SelectedContactsViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TAPRoomListModel>(parent, itemLayoutId) {
        private val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
        private val ivAvatarIcon: ImageView = itemView.findViewById(R.id.iv_avatar_icon)
        private val tvAvatarLabel: TextView = itemView.findViewById(R.id.tv_avatar_label)
        private val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
        override fun onBind(item: TAPRoomListModel, position: Int) {
            val room = item.lastMessage.room ?: return
            if (null != room.roomImage && room.roomImage!!.thumbnail.isNotEmpty()) {
                Glide.with(itemView.context)
                        .load(room.roomImage!!.thumbnail)
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                                // Show initial
                                if (itemView.context is Activity) {
                                    (itemView.context as Activity).runOnUiThread {
                                        ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.context, room.roomName)))
                                        civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                                        tvAvatarLabel.text = TAPUtils.getInitials(room.roomName, 2)
                                        tvAvatarLabel.visibility = View.VISIBLE
                                    }
                                }
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                return false
                            }
                        })
                        .into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            } else {
                // Show initial
                Glide.with(itemView.context).clear(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.context, room.roomName)))
                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                tvAvatarLabel.text = TAPUtils.getInitials(room.roomName, 2)
                tvAvatarLabel.visibility = View.VISIBLE
            }

            // Set name
            val fullName = room.roomName
            if (fullName.contains(" ")) {
                tvFullName.text = fullName.substring(0, fullName.indexOf(' '))
            } else {
                tvFullName.text = fullName
            }

            // Update avatar icon
            ivAvatarIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_remove_red_circle_background))
            ImageViewCompat.setImageTintList(ivAvatarIcon, ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconRemoveItemBackground)))
            ivAvatarIcon.background = ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_remove_item)
            ivAvatarIcon.visibility = View.VISIBLE
            itemView.setOnClickListener {
                listener.onRoomDeselected(item, position)
                notifyItemChanged(position)
            }
        }
    }

}