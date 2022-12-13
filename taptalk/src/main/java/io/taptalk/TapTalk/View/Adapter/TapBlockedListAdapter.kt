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
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Listener.TAPGeneralListener
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R

class TapBlockedListAdapter (users: List<TAPUserModel?>, private val listener: TAPGeneralListener<TAPUserModel>) :
    TAPBaseAdapter<TAPUserModel, TAPBaseViewHolder<TAPUserModel>>() {

    private var viewState = State.VIEW

    private enum class State {
        VIEW, EDIT
    }

    init {
        items = users
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TAPUserModel> {
        return BlockedViewHolder(parent, R.layout.tap_cell_user_contact)
    }

    inner class BlockedViewHolder(parent: ViewGroup, viewType: Int) : TAPBaseViewHolder<TAPUserModel>(parent, viewType) {

        private val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
        private val ivSelection: ImageView = itemView.findViewById(R.id.iv_selection)
        private val tvAvatarLabel: TextView = itemView.findViewById(R.id.tv_avatar_label)
        private val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private val vSeparator: View = itemView.findViewById(R.id.v_separator)
        override fun onBind(item: TAPUserModel?, position: Int) {
            if (item == null) return
            if (null != item.deleted && item.deleted!! > 0L) {
                // Deleted user
                Glide.with(itemView.context).load(R.drawable.tap_ic_deleted_user).fitCenter()
                    .into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            } else if (null != item.imageURL && item.imageURL.thumbnail.isNotEmpty()) {
                // Load profile picture
                Glide.with(itemView.context)
                    .load(item.imageURL.thumbnail)
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            // Show initial
                            if (itemView.context is Activity) {
                                (itemView.context as Activity).runOnUiThread {
                                    ImageViewCompat.setImageTintList(
                                        civAvatar,
                                        ColorStateList.valueOf(
                                            TAPUtils.getRandomColor(
                                                itemView.context,
                                                item.fullname
                                            )
                                        )
                                    )
                                    civAvatar.setImageDrawable(
                                        ContextCompat.getDrawable(
                                            itemView.context,
                                            R.drawable.tap_bg_circle_9b9b9b
                                        )
                                    )
                                    tvAvatarLabel.text = TAPUtils.getInitials(
                                        item.fullname,
                                        2
                                    )
                                    tvAvatarLabel.visibility = View.VISIBLE
                                }
                            }
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            } else {
                // Show initial
                Glide.with(itemView.context).clear(civAvatar)
                ImageViewCompat.setImageTintList(
                    civAvatar,
                    ColorStateList.valueOf(
                        TAPUtils.getRandomColor(
                            itemView.context,
                            item.fullname
                        )
                    )
                )
                civAvatar.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.tap_bg_circle_9b9b9b
                    )
                )
                tvAvatarLabel.text = TAPUtils.getInitials(item.fullname, 2)
                tvAvatarLabel.visibility = View.VISIBLE
            }
            tvFullName.text = item.fullname
            tvUsername.visibility = View.GONE

            // Remove separator on last item
            if (position == itemCount - 1 ) {
                vSeparator.visibility = View.GONE
            } else {
                vSeparator.visibility = View.VISIBLE
            }

            // Show/hide unblock
            if (viewState == State.EDIT) {
                ivSelection.setImageDrawable(
                    ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.tap_ic_demote_admin
                    )
                )
                ImageViewCompat.setImageTintList(
                    ivSelection,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            TapTalk.appContext,
                            R.color.tapButtonIconDestructiveColor
                        )
                    )
                )
                ivSelection.visibility = View.VISIBLE
            } else {
                ivSelection.visibility = View.GONE
            }

            itemView.setOnClickListener {
                if (viewState == State.VIEW) {
                    listener.onClick(position)
                }
            }
            ivSelection.setOnClickListener {
                if (viewState == State.EDIT) {
                    listener.onClick(position, item)
                }
            }
        }
    }

    fun isEditState() : Boolean {
        return viewState == State.EDIT
    }

    fun setViewState() {
        viewState = State.VIEW
        notifyDataSetChanged()
    }

    fun setEditState() {
        viewState = State.EDIT
        notifyDataSetChanged()
    }
}