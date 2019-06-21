package io.taptalk.TapTalk.View.Adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.taptalk.TapTalk.Helper.CircleImageView
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder
import io.taptalk.TapTalk.Model.TAPUserModel
import io.taptalk.Taptalk.R

class TAPGroupMemberAdapter(members: List<TAPUserModel>) : TAPBaseAdapter<TAPUserModel, TAPBaseViewHolder<TAPUserModel>>() {

    init {
        items = members
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TAPBaseViewHolder<TAPUserModel> {
        return MemberViewHolder(this, parent, R.layout.tap_cell_user_contact)
    }

    class MemberViewHolder(adapter: TAPGroupMemberAdapter, parent: ViewGroup, itemLayoutId: Int) : TAPBaseViewHolder<TAPUserModel>(parent, itemLayoutId) {
        private var civAvatar : CircleImageView = itemView.findViewById(R.id.civ_avatar)
        private var tvFullName : TextView = itemView.findViewById(R.id.tv_full_name)
        private var tvMemberRole : TextView = itemView.findViewById(R.id.tv_member_role)
        private var vSeparator : View = itemView.findViewById(R.id.v_separator)
        private val groupAdapter = adapter

        override fun onBind(item: TAPUserModel?, position: Int) {
            //activate / show member role (Admin)
            tvMemberRole.visibility = View.VISIBLE

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

        }

    }

}