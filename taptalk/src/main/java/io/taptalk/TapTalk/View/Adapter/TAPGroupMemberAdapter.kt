package io.taptalk.TapTalk.View.Adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.taptalk.TapTalk.Helper.CircleImageView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Interface.TapTalkGroupMemberListInterface
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.Taptalk.R

class TAPGroupMemberAdapter(cellMode: Int, members: List<TAPUserModel>, groupInterface: TapTalkGroupMemberListInterface) : TAPBaseAdapter<TAPUserModel, TAPBaseViewHolder<TAPUserModel>>() {

    init {
        items = members
    }
    var cellMode = cellMode
    val groupInterface = groupInterface

    companion object {
        const val NORMAL_MODE = 1
        const val SELECT_MODE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TAPUserModel> {
        return MemberViewHolder(this, parent, R.layout.tap_cell_user_contact)
    }

    fun updateCellMode(cellMode: Int) {
        this.cellMode = cellMode
        notifyDataSetChanged()
    }

    class MemberViewHolder(adapter: TAPGroupMemberAdapter, parent: ViewGroup, itemLayoutId: Int) : TAPBaseViewHolder<TAPUserModel>(parent, itemLayoutId) {
        private val civAvatar : CircleImageView = itemView.findViewById(R.id.civ_avatar)
        private val tvFullName : TextView = itemView.findViewById(R.id.tv_full_name)
        private val tvMemberRole : TextView = itemView.findViewById(R.id.tv_member_role)
        private val vSeparator : View = itemView.findViewById(R.id.v_separator)
        private val ivSelection : ImageView = itemView.findViewById(R.id.iv_selection)
        private val groupAdapter = adapter

        override fun onBind(item: TAPUserModel?, position: Int) {
            //activate / show member role (Admin)
            when(position) {
                in 0..1 -> tvMemberRole.visibility = View.VISIBLE
                else -> tvMemberRole.visibility = View.GONE
            }

            //load member avatar
            if (item?.avatarURL?.thumbnail.isNullOrEmpty()) {
                civAvatar.setImageDrawable(itemView.context.getDrawable(R.drawable.tap_img_default_avatar))
            } else {
                Glide.with(itemView.context)
                        .load(item?.avatarURL?.thumbnail)
                        .apply(RequestOptions().centerCrop())
                        .into(civAvatar)
            }

            //set name
            tvFullName.text = item?.name ?: ""

            //hide separator on last item
            if (position == groupAdapter.itemCount - 1) vSeparator.visibility = View.GONE
            else vSeparator.visibility = View.VISIBLE

            //Show and Hide Selection
            if (groupAdapter.cellMode == SELECT_MODE) {
                ivSelection.visibility = View.VISIBLE
            } else {
                ivSelection.visibility = View.GONE
            }

            //Set Listener for long press
            itemView.setOnLongClickListener{
                if (NORMAL_MODE == groupAdapter.cellMode) {
                    groupAdapter.groupInterface.onContactLongPress(item)
                    return@setOnLongClickListener true
                }
                return@setOnLongClickListener false
            }

        }

    }

}