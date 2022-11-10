package io.taptalk.TapTalk.View.Adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Typeface.BOLD
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
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
import io.taptalk.TapTalk.Helper.TAPTimeFormatter
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Model.TapMessageInfoModel
import io.taptalk.TapTalk.R

// TODO: update model after api MU
class TapMessageInfoAdapter (users: List<TapMessageInfoModel?>) :
    TAPBaseAdapter<TapMessageInfoModel, TAPBaseViewHolder<TapMessageInfoModel>>() {

    init {
        items = users
    }

    companion object {
        private const val SECTION = 1
        private const val MESSAGE_INFO = 2

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TapMessageInfoModel> {
        return when (viewType) {
            SECTION -> SectionViewHolder(parent, R.layout.tap_cell_message_info_section)
            else -> MessageInfoViewHolder(parent, R.layout.tap_cell_message_info)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val user = getItemAt(position).user
        return if (user == null) SECTION
        else MESSAGE_INFO
    }

    inner class MessageInfoViewHolder(parent: ViewGroup, viewType: Int) : TAPBaseViewHolder<TapMessageInfoModel>(parent, viewType) {
        val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
        val tvAvatarLabel: TextView = itemView.findViewById(R.id.tv_avatar_label)
        val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
        val vSeparator: View = itemView.findViewById(R.id.v_separator)
        val tvRead: TextView = itemView.findViewById(R.id.tv_read)
        val tvDelivered: TextView = itemView.findViewById(R.id.tv_delivered)

        override fun onBind(item: TapMessageInfoModel?, position: Int) {
            val user = item?.user
            if (null != user?.deleted && user.deleted!! > 0L) {
                // Deleted user
                Glide.with(itemView.context).load(R.drawable.tap_ic_deleted_user).fitCenter().into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            } else if (user?.imageURL?.thumbnail.isNullOrEmpty()) {
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.context, user?.fullname)))
                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                tvAvatarLabel.text = TAPUtils.getInitials(user?.fullname, 2)
                tvAvatarLabel.visibility = View.VISIBLE
            } else {
                Glide.with(itemView.context)
                    .load(user?.imageURL?.thumbnail)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            if (itemView.context is Activity) {
                                (itemView.context as Activity).runOnUiThread {
                                    ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(
                                        TAPUtils.getRandomColor(itemView.context, user?.fullname)))
                                    civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                                    tvAvatarLabel.text = TAPUtils.getInitials(user?.fullname, 2)
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
            tvFullName.text = user?.fullname ?: ""

            // Hide separator on last item
            if ((getItemViewType(itemCount - 1) == 1 &&
                        position == itemCount - 2) ||
                position == itemCount - 1) {
                vSeparator.visibility = View.GONE
            } else {
                vSeparator.visibility = View.VISIBLE
            }

            val datePattern = "dd/MM/yyyy HH:mm"
            if (item?.readTime != null && item. readTime > 0) {
                tvRead.visibility = View.VISIBLE
                val readTimeString = "read ${TAPTimeFormatter.formatTime(item.readTime, datePattern)}"
                val spannable = SpannableString(readTimeString)
                spannable.setSpan(
                    StyleSpan(BOLD),
                    4,
                    readTimeString.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                tvRead.text = spannable
            } else {
                tvRead.visibility = View.GONE
            }
            if (item?.deliveredTime != null && item. deliveredTime > 0) {
                val deliveredTimeString = "delivered ${TAPTimeFormatter.formatTime(item.deliveredTime, datePattern)}"
                val spannable = SpannableString(deliveredTimeString)
                spannable.setSpan(
                    StyleSpan(BOLD),
                    9,
                    deliveredTimeString.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                tvDelivered.text = spannable
            } else {
                itemView.visibility = View.GONE
            }

        }
    }

    inner class SectionViewHolder(parent: ViewGroup, viewType: Int) : TAPBaseViewHolder<TapMessageInfoModel>(parent, viewType) {
        val title: TextView = itemView.findViewById(R.id.tv_section_title)
        val icon: ImageView = itemView.findViewById(R.id.iv_section_icon)
        override fun onBind(item: TapMessageInfoModel, position: Int) {
            if (item.readTime > -1) {
                // read section
                title.text = String.format(itemView.context.getString(R.string.tap_read_by_d_format), item.readTime)
                icon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_read_orange))
            } else {
                // delivered section
                title.text = String.format(itemView.context.getString(R.string.tap_delivered_to_d_format), item.deliveredTime)
                icon.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_delivered_grey))
            }
            // TODO: add tapUI for read by handler MU
        }
    }
}