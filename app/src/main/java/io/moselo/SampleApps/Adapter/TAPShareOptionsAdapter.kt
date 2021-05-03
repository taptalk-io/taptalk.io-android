package io.moselo.SampleApps.Adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.moselo.SampleApps.listener.TAPShareOptionsInterface
import io.taptalk.TapTalk.Const.TAPDefaultConstant
import io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType
import io.taptalk.TapTalk.DiffCallback.TAPRoomListDiffCallback
import io.taptalk.TapTalk.Helper.CircleImageView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Manager.TAPContactManager
import io.taptalk.TapTalk.Manager.TAPGroupManager.Companion.getInstance
import io.taptalk.TapTalk.Manager.TapUI
import io.taptalk.TapTalk.Model.TAPRoomListModel
import io.taptalk.TapTalk.Model.TAPRoomModel
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Adapter.TAPBaseAdapter
import io.taptalk.TapTalk.ViewModel.TAPShareOptionsViewModel

class TAPShareOptionsAdapter(val instanceKey: String, list: List<TAPRoomListModel>, private val vm: TAPShareOptionsViewModel, val glide: RequestManager, private val listener: TAPShareOptionsInterface): TAPBaseAdapter<TAPRoomListModel, TAPBaseViewHolder<TAPRoomListModel>>() {

    init {
        items = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TAPRoomListModel> {
        return when (TAPRoomListModel.Type.values()[viewType]) {
            TAPRoomListModel.Type.SELECTABLE_CONTACT -> ContactListViewHolder(parent, R.layout.tap_cell_user_contact)
            TAPRoomListModel.Type.SECTION -> SectionTitleViewHolder(parent, R.layout.tap_cell_section_title)
            else -> RoomListVH(parent, R.layout.tap_cell_user_room)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItemAt(position).type.ordinal
    }

    inner class RoomListVH(parent: ViewGroup, itemLayoutId: Int): TAPBaseViewHolder<TAPRoomListModel>(parent, itemLayoutId) {

        private val clContainer = itemView.findViewById<ConstraintLayout>(R.id.cl_container)
        private val civAvatar = itemView.findViewById<ImageView>(R.id.civ_avatar)
        private val ivAvatarIcon = itemView.findViewById<ImageView?>(R.id.iv_avatar_icon)
        private val ivMute = itemView.findViewById<ImageView?>(R.id.iv_mute)
        private val ivMessageStatus = itemView.findViewById<ImageView?>(R.id.iv_message_status)
        private val ivPersonalRoomTypingIndicator = itemView.findViewById<ImageView?>(R.id.iv_personal_room_typing_indicator)
        private val tvAvatarLabel = itemView.findViewById<TextView?>(R.id.tv_avatar_label)
        private val tvFullName = itemView.findViewById<TextView?>(R.id.tv_full_name)
        private val tvLastMessage = itemView.findViewById<TextView?>(R.id.tv_last_message)
        private val tvLastMessageTime = itemView.findViewById<TextView?>(R.id.tv_last_message_time)
        private val tvBadgeUnread = itemView.findViewById<TextView?>(R.id.tv_badge_unread)
        private val ivBadgeMention = itemView.findViewById<ImageView?>(R.id.iv_badge_mention)
        private val vSeparator = itemView.findViewById<View?>(R.id.v_separator)
        private val vSeparatorFull = itemView.findViewById<View?>(R.id.v_separator_full)
        private val gSelected = itemView.findViewById<Group?>(R.id.g_selected)

        override fun onBind(item: TAPRoomListModel?, position: Int) {
            val activeUser = TAPChatManager.getInstance(instanceKey).activeUser ?: return

            val room = item!!.lastMessage.room
            var user: TAPUserModel? = null
            var group: TAPRoomModel? = null

            if (room.roomType == RoomType.TYPE_PERSONAL) {
                user = TAPContactManager.getInstance(instanceKey).getUserData(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(room.roomID))
            } else if (room.roomType == RoomType.TYPE_GROUP || room.roomType == RoomType.TYPE_TRANSACTION) {
                group = getInstance(instanceKey).getGroupData(room.roomID)
            }

            // Set room image
            if (null != user && (null == user.deleted || user.deleted!! <= 0L) && null != user.avatarURL && user.avatarURL.thumbnail.isNotEmpty()) {
                // Load user avatar
                glide.load(user.avatarURL.thumbnail).listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        // Show initial
                        if (itemView.context is Activity) {
                            (itemView.context as Activity).runOnUiThread {
                                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(item.defaultAvatarBackgroundColor))
                                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                                tvAvatarLabel.text = TAPUtils.getInitials(room.roomName, if (room.roomType == RoomType.TYPE_PERSONAL) 2 else 1)
                                tvAvatarLabel.visibility = View.VISIBLE
                            }
                        }
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        return false
                    }
                }).into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            } else if (null != group && !group.isRoomDeleted && null != group.roomImage &&
                    group.roomImage!!.thumbnail.isNotEmpty()) {
                // Load group image
                glide.load(group.roomImage!!.thumbnail).into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            } else if (null != room.roomImage && room.roomImage!!.thumbnail.isNotEmpty()) {
                // Load room image
                glide.load(room.roomImage!!.thumbnail).into(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, null)
                tvAvatarLabel.visibility = View.GONE
            } else {
                // Show initial
                glide.clear(civAvatar)
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(item.defaultAvatarBackgroundColor))
                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_circle_9b9b9b))
                tvAvatarLabel.text = TAPUtils.getInitials(room.roomName, if (room.roomType == RoomType.TYPE_PERSONAL) 2 else 1)
                tvAvatarLabel.visibility = View.VISIBLE
            }

            clContainer.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.tapWhite))
            if (room.roomType == RoomType.TYPE_GROUP) {
                tvAvatarLabel.visibility = View.VISIBLE
                glide.load(R.drawable.tap_ic_group_icon).into(ivAvatarIcon)
            } else {
                ivAvatarIcon.visibility = View.GONE
            }

            // Show/hide separator
            if (position == itemCount - 1) {
                vSeparator.visibility = View.GONE
                vSeparatorFull.visibility = View.VISIBLE
            } else {
                vSeparator.visibility = View.VISIBLE
                vSeparatorFull.visibility = View.GONE
            }

            // Set room name
            if (null != user && (null == user.deleted || user.deleted!! <= 0L) && null != user.name && user.name.isNotEmpty()) {
                tvFullName.text = user.name
            } else if (null != group && !group.isRoomDeleted && null != group.roomName &&
                    group.roomName.isNotEmpty()) {
                tvFullName.text = group.roomName
            } else {
                tvFullName.text = room.roomName
            }

            // Set last message timestamp
            tvLastMessageTime.text = item.lastMessageTimestamp

            val draft = TAPChatManager.getInstance(instanceKey).getMessageFromDraft(item.lastMessage.room.roomID)
            if (null != draft && draft.isNotEmpty()) {
                // Show draft
                tvLastMessage.text = String.format(itemView.context.getString(R.string.tap_format_s_draft), draft)
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            } else if (0 < item.typingUsersSize && RoomType.TYPE_PERSONAL == item.lastMessage.room.roomType) {
                // Set message to Typing
                tvLastMessage.text = itemView.context.getString(R.string.tap_typing)
                ivPersonalRoomTypingIndicator.visibility = View.VISIBLE
                if (null == ivPersonalRoomTypingIndicator.drawable) {
                    glide.load(R.raw.gif_typing_indicator).into(ivPersonalRoomTypingIndicator)
                }
            } else if (1 == item.typingUsersSize && RoomType.TYPE_PERSONAL != item.lastMessage.room.roomType) {
                // Set message to typing
                val typingStatus = String.format(itemView.context.getString(R.string.tap_format_s_typing_single), item.firstTypingUserName)
                tvLastMessage.text = typingStatus
                tvLastMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.tapRoomListMessageColor))
                ivPersonalRoomTypingIndicator.visibility = View.VISIBLE
                if (null == ivPersonalRoomTypingIndicator.drawable) {
                    glide.load(R.raw.gif_typing_indicator).into(ivPersonalRoomTypingIndicator)
                }
            } else if (1 < item.typingUsersSize && RoomType.TYPE_PERSONAL != item.lastMessage.room.roomType) {
                // Set message to multiple users typing
                val typingStatus = String.format(itemView.context.getString(R.string.tap_format_d_people_typing), item.typingUsersSize)
                tvLastMessage.text = typingStatus
                tvLastMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.tapRoomListMessageColor))
                tvLastMessage.text = ""
                ivPersonalRoomTypingIndicator.visibility = View.VISIBLE
                if (null == ivPersonalRoomTypingIndicator.drawable) {
                    glide.load(R.raw.gif_typing_indicator).into(ivPersonalRoomTypingIndicator)
                }
            } else if (null != item.lastMessage.user && activeUser.userID == item.lastMessage.user.userID && null != item.lastMessage.isDeleted && item.lastMessage.isDeleted!!) {
                // Show last message deleted by active user
                tvLastMessage.text = itemView.resources.getString(R.string.tap_you_deleted_this_message)
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            } else if (null != item.lastMessage.isDeleted && item.lastMessage.isDeleted!!) {
                // Show last message deleted by sender
                tvLastMessage.text = itemView.resources.getString(R.string.tap_this_deleted_message)
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            } else if (TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE == item.lastMessage.type) {
                // Show system message
                tvLastMessage.text = TAPChatManager.getInstance(instanceKey).formattingSystemMessage(item.lastMessage)
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            } else if (item.lastMessage.room.roomType != RoomType.TYPE_PERSONAL) {
                // Show group/channel room with last message
                val sender = if (activeUser.userID == item.lastMessage.user.userID) itemView.context.getString(R.string.tap_you) else TAPUtils.getFirstWordOfString(item.lastMessage.user.name)
                tvLastMessage.text = String.format("%s: %s", sender, item.lastMessage.body)
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            } else {
                // Show personal room with last message
                tvLastMessage.text = item.lastMessage.body
                ivPersonalRoomTypingIndicator.visibility = View.GONE
            }

            // Check if room is muted
            if (room.isMuted) {
                ivMute.visibility = View.VISIBLE
                tvBadgeUnread.background = ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_room_list_unread_badge_inactive)
            } else {
                ivMute.visibility = View.GONE
                tvBadgeUnread.background = ContextCompat.getDrawable(itemView.context, R.drawable.tap_bg_room_list_unread_badge)
            }

            // Change Status Message Icon
            // Message sender is not the active user / last message is system message / room draft exists
            if (null != item.lastMessage && (item.lastMessage.user.userID != activeUser.userID ||
                            TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE == item.lastMessage.type) || null != draft && draft.isNotEmpty()) {
                ivMessageStatus.setImageDrawable(null)
            } else if (null != item.lastMessage && null != item.lastMessage.isDeleted && item.lastMessage.isDeleted!!) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_block_red))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageDeleted)))
            } else if (null != item.lastMessage && null != item.lastMessage.isRead && item.lastMessage.isRead!! && !TapUI.getInstance(instanceKey).isReadStatusHidden) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_read_orange))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageRead)))
            } else if (null != item.lastMessage && null != item.lastMessage.delivered && item.lastMessage.delivered!!) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_delivered_grey))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageDelivered)))
            } else if (null != item.lastMessage && null != item.lastMessage.failedSend && item.lastMessage.failedSend!!) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_warning_red_circle_background))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageFailed)))
            } else if (null != item.lastMessage && null != item.lastMessage.sending && !item.lastMessage.sending!!) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_sent_grey))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageSent)))
            } else if (null != item.lastMessage && null != item.lastMessage.sending && item.lastMessage.sending!!) {
                ivMessageStatus.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_sending_grey))
                ImageViewCompat.setImageTintList(ivMessageStatus, ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.tapIconRoomListMessageSending)))
            }

            // Show unread count
            val unreadCount = item.unreadCount
            when {
                unreadCount in 1..99 -> {
                    tvBadgeUnread.text = item.unreadCount.toString()
                    ivMessageStatus.visibility = View.GONE
                    tvBadgeUnread.visibility = View.VISIBLE
                }
                unreadCount >= 100 -> {
                    tvBadgeUnread.setText(R.string.tap_over_99)
                    ivMessageStatus.visibility = View.GONE
                    tvBadgeUnread.visibility = View.VISIBLE
                }
                else -> {
                    ivMessageStatus.visibility = View.VISIBLE
                    tvBadgeUnread.visibility = View.GONE
                }
            }

            // Show mention badge
            if (!TapUI.getInstance(instanceKey).isMentionUsernameDisabled && item.unreadMentions > 0) {
                ivBadgeMention.visibility = View.VISIBLE
            } else {
                ivBadgeMention.visibility = View.GONE
            }

            if (vm.selectedRooms!!.containsKey(room.roomID)) {
                gSelected.visibility = View.VISIBLE
                itemView.setOnClickListener {
                    listener.onRoomDeselected(item, position)
                    notifyItemChanged(adapterPosition)
                }
            } else {
                gSelected.visibility = View.GONE
                itemView.setOnClickListener {
                    listener.onRoomSelected(item)
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    // TODO: 30/04/21 remove bottom line on each list MU
    inner class ContactListViewHolder(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TAPRoomListModel>(parent, itemLayoutId) {
        private val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
        private val ivAvatarIcon: ImageView = itemView.findViewById(R.id.iv_avatar_icon)
        private val ivSelection: ImageView = itemView.findViewById(R.id.iv_selection)
        private val tvAvatarLabel: TextView = itemView.findViewById(R.id.tv_avatar_label)
        private val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private val vSeparator: View = itemView.findViewById(R.id.v_separator)
        override fun onBind(item: TAPRoomListModel, position: Int) {
            val room = item.lastMessage.room ?: return
            if (null != room.roomImage && room.roomImage!!.thumbnail.isNotEmpty()) {
                // Load profile picture
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
            tvFullName.text = room.roomName
            tvUsername.visibility = View.GONE
            ivSelection.visibility = View.VISIBLE

            // Remove separator on last item
//            if (position == getItemCount() - 1 || getItemAt(position + 1).getType() != item.type) {
//                vSeparator.visibility = View.GONE
//            } else {
//                vSeparator.visibility = View.VISIBLE
//            }

            if (vm.selectedRooms!!.containsKey(room.roomID)) {
                ivSelection.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_circle_active))
                ImageViewCompat.setImageTintList(ivSelection, ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconCircleSelectionActive)))
                itemView.setOnClickListener {
                    listener.onRoomDeselected(item, position)
                    notifyItemChanged(adapterPosition)
                }
            } else {
                ivSelection.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_circle_inactive))
                ImageViewCompat.setImageTintList(ivSelection, ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconCircleSelectionInactive)))
                itemView.setOnClickListener {
                    listener.onRoomSelected(item)
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    class SectionTitleViewHolder internal constructor(parent: ViewGroup?, itemLayoutId: Int) : TAPBaseViewHolder<TAPRoomListModel>(parent, itemLayoutId) {
        private val tvRecentTitle: TextView = itemView.findViewById(R.id.tv_section_title)
        override fun onBind(item: TAPRoomListModel, position: Int) {
            tvRecentTitle.text = item.title
        }

    }

    fun addRoomList(roomList: List<TAPRoomListModel?>?) {
        val diffCallback = TAPRoomListDiffCallback(items, roomList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        setItemsWithoutNotify(roomList)
        diffResult.dispatchUpdatesTo(this)
    }
}