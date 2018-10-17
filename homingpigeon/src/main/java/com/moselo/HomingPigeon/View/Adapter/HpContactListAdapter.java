package com.moselo.HomingPigeon.View.Adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.CircleImageView;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Interface.ContactListInterface;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class HpContactListAdapter extends HpBaseAdapter<HpUserModel, HpBaseViewHolder<HpUserModel>> {

    private ContactListInterface listener;
    private ColorStateList avatarTint;
    private String myID;
    private int viewType;
    private boolean isAnimating = true;

    public static final int NONE = 0;
    public static final int CHAT = 1;
    public static final int SELECT = 2;
    public static final int SELECTED_MEMBER = 3;

    public HpContactListAdapter(int viewType, List<HpUserModel> contactList) {
        setItems(contactList, false);
        this.viewType = viewType;
        this.myID = HpDataManager.getInstance().getActiveUser().getUserID();
    }

    public HpContactListAdapter(int viewType, List<HpUserModel> contactList, @Nullable ContactListInterface listener) {
        setItems(contactList, false);
        this.viewType = viewType;
        this.listener = listener;
        this.myID = HpDataManager.getInstance().getActiveUser().getUserID();
    }

    @NonNull
    @Override
    public HpBaseViewHolder<HpUserModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case SELECTED_MEMBER:
                return new SelectedGroupMemberHolder(parent, R.layout.hp_cell_group_member);
            default:
                return new ContactListHolder(parent, R.layout.hp_cell_user_contact);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    class ContactListHolder extends HpBaseViewHolder<HpUserModel> {

        private CircleImageView ivAvatar;
        private ImageView ivAvatarIcon, ivSelection;
        private TextView tvFullName;
        private View vSeparator;

        ContactListHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivSelection = itemView.findViewById(R.id.iv_selection);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            vSeparator = itemView.findViewById(R.id.v_separator);
        }

        @Override
        protected void onBind(HpUserModel item, int position) {
            final int randomColor = HpUtils.getInstance().getRandomColor(item.getName());

            if (null != item.getAvatarURL()) {
                GlideApp.with(itemView.getContext()).load(item.getAvatarURL().getThumbnail()).centerCrop().into(ivAvatar);
            } else {
                avatarTint = ColorStateList.valueOf(randomColor);
                ivAvatar.setBackgroundTintList(avatarTint);
            }

            // Change avatar icon and background
            // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE
            if (null != item.getAvatarURL())
                GlideApp.with(itemView.getContext()).load(item.getAvatarURL().getThumbnail()).centerCrop().into(ivAvatar);

            // Set name
            tvFullName.setText(item.getName());

            // Remove separator on last item
            if (position == getItemCount() - 1) {
                vSeparator.setVisibility(View.GONE);
            } else {
                vSeparator.setVisibility(View.VISIBLE);
            }

            // Show/hide selection
            if (viewType == SELECT && item.isSelected()) {
                ivSelection.setVisibility(View.VISIBLE);
                ivSelection.setImageResource(R.drawable.hp_ic_circle_active);
            } else if (viewType == SELECT && !item.isSelected()) {
                ivSelection.setVisibility(View.VISIBLE);
                ivSelection.setImageResource(R.drawable.hp_ic_circle_inactive);
            } else {
                ivSelection.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                switch (viewType) {
                    case CHAT:
                        if (!myID.equals(item.getUserID())) {
                            HpUtils.getInstance().startChatActivity(
                                    itemView.getContext(),
                                    HpChatManager.getInstance().arrangeRoomId(myID, item.getUserID()),
                                    item.getName(),
                                    item.getAvatarURL(),
                                    1,
                                    /* TEMPORARY ROOM COLOR */randomColor + "");
                        }
                        break;
                    case SELECT:
                        if (null != listener && listener.onContactSelected(item, !item.isSelected())) {
                            isAnimating = true;
                            item.setSelected(!item.isSelected());
                            notifyItemChanged(position);
                        }
                        break;
                }
            });
        }
    }

    class SelectedGroupMemberHolder extends HpBaseViewHolder<HpUserModel> {

        private CircleImageView ivAvatar;
        private ImageView ivAvatarIcon;
        private TextView tvFullName;

        SelectedGroupMemberHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
        }

        @Override
        protected void onBind(HpUserModel item, int position) {
            final int randomColor = HpUtils.getInstance().getRandomColor(item.getName());

            if (null != item.getAvatarURL()) {
                GlideApp.with(itemView.getContext()).load(item.getAvatarURL().getThumbnail()).centerCrop().into(ivAvatar);
            } else {
                avatarTint = ColorStateList.valueOf(randomColor);
                ivAvatar.setBackgroundTintList(avatarTint);
            }

            // Set name
            String fullName = item.getName();
            if (item.getUserID().equals(myID)) {
                tvFullName.setText(R.string.you);
            } else if (fullName.contains(" ")) {
                tvFullName.setText(fullName.substring(0, fullName.indexOf(' ')));
            } else tvFullName.setText(fullName);

            // TODO: 19 September 2018 UPDATE EXPERT ICON
            // Update avatar icon
            if ((null == listener || item.getUserID().equals(myID)) /*&& item.getUserRole().equals("1")*/) {
                ivAvatarIcon.setVisibility(View.GONE);
            } else if ((null == listener || item.getUserID().equals(myID)) /*&& item.getUserRole().equals("2")*/) {
                ivAvatarIcon.setVisibility(View.VISIBLE);
                ivAvatarIcon.setImageResource(R.drawable.hp_ic_verified);
            } else {
                ivAvatarIcon.setVisibility(View.VISIBLE);
                ivAvatarIcon.setImageResource(R.drawable.hp_ic_close_red_circle);
            }

            itemView.setOnClickListener(v -> {
                if (null != listener && !item.getUserID().equals(myID) && !isAnimating) {
                    isAnimating = true;
                    listener.onContactRemoved(item);
                }
            });
        }
    }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }
}