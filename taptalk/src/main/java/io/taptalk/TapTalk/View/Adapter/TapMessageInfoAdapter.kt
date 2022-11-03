package io.taptalk.TapTalk.View.Adapter

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
import io.taptalk.TapTalk.Helper.CircleImageView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R

// TODO: update model after api MU
class TapMessageInfoAdapter (users: List<TAPUserModel?>) :
    TAPBaseAdapter<TAPUserModel, TAPBaseViewHolder<TAPUserModel>>() {

    init {
        items = users
    }

    companion object {
        private const val SECTION = 1
        private const val MESSAGE_INFO = 2

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TAPUserModel> {
        return when (viewType) {
            SECTION -> SectionViewHolder(parent, R.layout.tap_cell_message_info_section)
            else -> MessageInfoViewHolder(parent, R.layout.tap_cell_message_info)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val id = getItemAt(position).userID
        return if (id.isEmpty()) SECTION
        else MESSAGE_INFO
    }

    inner class MessageInfoViewHolder(parent: ViewGroup, viewType: Int) : TAPBaseViewHolder<TAPUserModel>(parent, viewType) {
        override fun onBind(item: TAPUserModel?, position: Int) {
            val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
            val tvAvatarLabel: TextView = itemView.findViewById(R.id.tv_avatar_label)
            val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
            val vSeparator: View = itemView.findViewById(R.id.v_separator)
            val tvRead: TextView = itemView.findViewById(R.id.tv_read)
            val tvDelivered: TextView = itemView.findViewById(R.id.tv_delivered)

            if (null != item?.deleted && item.deleted!! > 0L) {
                // Deleted user
                Glide.with(itemView.context).load(R.drawable.tap_ic_deleted_user).fitCenter().into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            } else if (item?.imageURL?.thumbnail.isNullOrEmpty()) {
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.context, item?.fullname)))
                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                tvAvatarLabel.text = TAPUtils.getInitials(item?.fullname, 2)
                tvAvatarLabel.visibility = View.VISIBLE
            } else {
                Glide.with(itemView.context)
                    .load(item?.imageURL?.thumbnail)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            if (itemView.context is Activity) {
                                (itemView.context as Activity).runOnUiThread {
                                    ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(
                                        TAPUtils.getRandomColor(itemView.context, item?.fullname)))
                                    civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                                    tvAvatarLabel.text = TAPUtils.getInitials(item?.fullname, 2)
                                    tvAvatarLabel.visibility = View.VISIBLE
                                }
                            }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                    })
                    .into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            }

            // Set name
            tvFullName.text = item?.fullname ?: ""

            // Hide separator on last item
            if ((getItemViewType(itemCount - 1) == 1 &&
                        position == itemCount - 2) ||
                position == itemCount - 1) {
                vSeparator.visibility = View.GONE
            } else {
                vSeparator.visibility = View.VISIBLE
            }

        }
    }

    inner class SectionViewHolder(parent: ViewGroup, viewType: Int) : TAPBaseViewHolder<TAPUserModel>(parent, viewType) {
        override fun onBind(item: TAPUserModel?, position: Int) {
            val title = itemView.findViewById<TextView>(R.id.tv_section_title)
            val icon  = itemView.findViewById<ImageView>(R.id.iv_section_icon)
            // TODO: differentiate read and delivered MU
            // TODO: add tapUI for read by handler MU
        }
    }
}