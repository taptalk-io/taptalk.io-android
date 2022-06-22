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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.taptalk.TapTalk.DiffCallback.TAPGroupMemberDiffCallback
import io.taptalk.TapTalk.Helper.CircleImageView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Helper.TAPUtils
import io.taptalk.TapTalk.Helper.TapTalk
import io.taptalk.TapTalk.Listener.TAPGroupMemberListListener
import io.taptalk.TapTalk.Manager.TAPChatManager
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.TapTalk.R
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity
import java.lang.Exception

class TAPGroupMemberAdapter(cellMode: Int, members: List<TAPUserModel>, adminList: List<String>, groupInterface: TAPGroupMemberListListener) : TAPBaseAdapter<TAPUserModel, TAPBaseViewHolder<TAPUserModel>>() {

    var adminList: MutableList<String> = mutableListOf()

    init {
        items = members
        this.adminList = adminList.toMutableList()
    }

    var cellMode = cellMode
    val groupInterface = groupInterface

    companion object {
        const val NORMAL_MODE = 1
        const val SELECT_MODE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TAPUserModel> {
        return when (viewType) {
            1 ->
                CountViewHolder(parent, R.layout.tap_cell_group_member_count)
            else ->
                MemberViewHolder(this, parent, R.layout.tap_cell_user_contact)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position >= (itemCount - 1) && getItemAt(position).userID.isEmpty()) {
            return 1
        }
        return 0
    }

    fun updateCellMode(cellMode: Int) {
        this.cellMode = cellMode
        notifyDataSetChanged()
    }

    fun setMemberItems(newList: List<TAPUserModel>) {
        val diffResult: DiffResult = DiffUtil.calculateDiff(TAPGroupMemberDiffCallback(items, newList), true)
        setItemsWithoutNotify(newList)
        diffResult.dispatchUpdatesTo(this@TAPGroupMemberAdapter)
    }

    class MemberViewHolder(adapter: TAPGroupMemberAdapter, parent: ViewGroup, itemLayoutId: Int) : TAPBaseViewHolder<TAPUserModel>(parent, itemLayoutId) {
        private val civAvatar: CircleImageView = itemView.findViewById(R.id.civ_avatar)
        private val tvAvatarLabel: TextView = itemView.findViewById(R.id.tv_avatar_label)
        private val tvFullName: TextView = itemView.findViewById(R.id.tv_full_name)
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private val tvMemberRole: TextView = itemView.findViewById(R.id.tv_member_role)
        private val vSeparator: View = itemView.findViewById(R.id.v_separator)
        private val ivSelection: ImageView = itemView.findViewById(R.id.iv_selection)
        private val groupAdapter = adapter
        private var isAdmin: Boolean = false

        override fun onBind(item: TAPUserModel?, position: Int) {
            val instanceKey = try {
                val activity = itemView.context as TAPBaseActivity
                activity.instanceKey
            } catch (e: Exception) {
                ""
            }

            if (groupAdapter.adminList.isNotEmpty() && groupAdapter.adminList.contains(item?.userID
                            ?: "0")) {
                isAdmin = true
                tvMemberRole.visibility = View.VISIBLE
            } else {
                isAdmin = false
                tvMemberRole.visibility = View.GONE
            }

            // Load member avatar
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
                                        ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.context, item?.fullname)))
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
            tvUsername.visibility = View.GONE

            // Hide separator on last item
            if ((groupAdapter.getItemViewType(groupAdapter.itemCount - 1) == 1 &&
                            position == groupAdapter.itemCount - 2) ||
                    position == groupAdapter.itemCount - 1) {
                vSeparator.visibility = View.GONE
            } else {
                vSeparator.visibility = View.VISIBLE
            }

            // Show or hide selection
            if (groupAdapter.cellMode == SELECT_MODE &&
                    item?.userID != TAPChatManager.getInstance(instanceKey).activeUser.userID) {
                ivSelection.visibility = View.VISIBLE
            } else {
                ivSelection.visibility = View.GONE
            }
            if (SELECT_MODE == groupAdapter.cellMode && true == item?.isSelected) {
                ivSelection.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_circle_active))
                ImageViewCompat.setImageTintList(ivSelection, ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconCircleSelectionActive)))
            } else {
                ivSelection.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_circle_inactive))
                ImageViewCompat.setImageTintList(ivSelection, ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconCircleSelectionInactive)))
            }

            // Set listener for click
            itemView.setOnClickListener {
                if (SELECT_MODE == groupAdapter.cellMode && false == item?.isSelected &&
                        item.userID != TAPChatManager.getInstance(instanceKey).activeUser.userID) {
                    groupAdapter.groupInterface.onContactSelected(item)
                    item.isSelected = true
                    ivSelection.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_circle_active))
                    ImageViewCompat.setImageTintList(ivSelection, ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconCircleSelectionActive)))
                } else if (SELECT_MODE == groupAdapter.cellMode && true == item?.isSelected &&
                        item.userID != TAPChatManager.getInstance(instanceKey).activeUser.userID) {
                    groupAdapter.groupInterface.onContactDeselected(item)
                    item.isSelected = false
                    ivSelection.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.tap_ic_circle_inactive))
                    ImageViewCompat.setImageTintList(ivSelection, ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconCircleSelectionInactive)))
                } else if (NORMAL_MODE == groupAdapter.cellMode) {
                    groupAdapter.groupInterface.onGroupMemberClicked(item, isAdmin)
                }
            }

            // Set listener for long press
            itemView.setOnLongClickListener {
                if (NORMAL_MODE == groupAdapter.cellMode && item?.userID != TAPChatManager.getInstance(instanceKey).activeUser.userID) {
                    item?.isSelected = true
                    groupAdapter.groupInterface.onContactLongPress(item)
                    return@setOnLongClickListener true
                }
                return@setOnLongClickListener false
            }
        }
    }

    class CountViewHolder(parent: ViewGroup, itemLayoutId: Int) : TAPBaseViewHolder<TAPUserModel>(parent, itemLayoutId) {

        private val tvMemberCount: TextView = itemView.findViewById(R.id.tv_member_count)

        override fun onBind(item: TAPUserModel?, position: Int) {
            tvMemberCount.text = String.format(itemView.context.getString(R.string.tap_format_d_group_member_count), position)
        }
    }
}
