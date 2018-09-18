package com.moselo.HomingPigeon.View.Adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.BaseViewHolder;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.ContactListListener;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class ContactListAdapter extends BaseAdapter<UserModel, BaseViewHolder<UserModel>> {

    private ContactListListener listener;
    private ColorStateList avatarTint;
    private int viewType;

    public static final int NONE = 0;
    public static final int CHAT = 1;
    public static final int SELECT = 2;
    public static final int SELECTED_MEMBER = 3;

    public ContactListAdapter(int viewType, List<UserModel> contactList) {
        setItems(contactList, false);
        this.viewType = viewType;
    }

    public ContactListAdapter(int viewType, List<UserModel> contactList, @Nullable ContactListListener listener) {
        setItems(contactList, false);
        this.viewType = viewType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder<UserModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case SELECTED_MEMBER:
                return new SelectedGroupMemberHolder(parent, R.layout.cell_group_member);
            default:
                return new ContactListHolder(parent, R.layout.cell_user_contact);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    class ContactListHolder extends BaseViewHolder<UserModel> {

        private ImageView ivAvatar, ivAvatarIcon, ivSelection;
        private TextView tvFullName;
        private View vSeparator;

        public ContactListHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivSelection = itemView.findViewById(R.id.iv_selection);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            vSeparator = itemView.findViewById(R.id.v_separator);
        }

        @Override
        protected void onBind(UserModel item, int position) {
            final int randomColor = Utils.getInstance().getRandomColor(item.getName());

            // TODO: 6 September 2018 LOAD AVATAR IMAGE TO VIEW
            avatarTint = ColorStateList.valueOf(randomColor);
            ivAvatar.setBackgroundTintList(avatarTint);

            // Change avatar icon and background
            // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE

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
                ivSelection.setImageResource(R.drawable.ic_circle_active);
            } else if (viewType == SELECT && !item.isSelected()) {
                ivSelection.setVisibility(View.VISIBLE);
                ivSelection.setImageResource(R.drawable.ic_circle_inactive);
            } else {
                ivSelection.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                switch (viewType) {
                    case CHAT:
                        // TODO: 17 September 2018 OPEN CHAT ROOM
                        break;
                    case SELECT:
                        if (listener.onContactSelected(item, !item.isSelected())) {
                            item.setSelected(!item.isSelected());
                            notifyItemChanged(position);
                        }
                        break;
                }
            });
        }
    }

    class SelectedGroupMemberHolder extends BaseViewHolder<UserModel> {

        private ImageView ivAvatar;
        private TextView tvFullName;

        protected SelectedGroupMemberHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
        }

        @Override
        protected void onBind(UserModel item, int position) {
            final int randomColor = Utils.getInstance().getRandomColor(item.getName());

            // TODO: 6 September 2018 LOAD AVATAR IMAGE TO VIEW
            avatarTint = ColorStateList.valueOf(randomColor);
            ivAvatar.setBackgroundTintList(avatarTint);

            // Set name
            String fullName = item.getName();
            if (fullName.contains(" ")) {
                tvFullName.setText(fullName.substring(0, fullName.indexOf(' ')));
            } else tvFullName.setText(fullName);

            itemView.setOnClickListener(v -> {
                removeItem(item);
                listener.onContactRemoved(item);
            });
        }
    }
}