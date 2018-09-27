package com.moselo.HomingPigeon.View.Adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.BaseViewHolder;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.ContactListListener;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.DataManager;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.ChatActivity;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.Extras.ROOM_NAME;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_COLOR;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_MY_USERNAME;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_ROOM_ID;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class ContactListAdapter extends BaseAdapter<UserModel, BaseViewHolder<UserModel>> {

    private ContactListListener listener;
    private ColorStateList avatarTint;
    private String myID;
    private int viewType;
    private boolean isAnimating = true;

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

    public ContactListAdapter(int viewType, List<UserModel> contactList, @Nullable ContactListListener listener, String myID) {
        setItems(contactList, false);
        this.viewType = viewType;
        this.listener = listener;
        this.myID = myID;
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

        ContactListHolder(ViewGroup parent, int itemLayoutId) {
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
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
                        UserModel myUser = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
                        }, prefs.getString(K_USER, ""));

                        String myUserID = myUser.getUserID();

                        ChatManager.getInstance().saveUnsentMessage();
                        Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
                        intent.putExtra(K_MY_USERNAME, myUser.getName());
                        intent.putExtra(ROOM_NAME, item.getName());
                        intent.putExtra(K_COLOR, randomColor);
                        // TODO: 26 September 2018 PASS ROOM ID
//                        intent.putExtra(K_ROOM_ID, item.getRoom().getRoomID());
//                        DataManager.getInstance().saveRecipientID(itemView.getContext(), item.getRecipientID());
                        itemView.getContext().startActivity(intent);
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

    class SelectedGroupMemberHolder extends BaseViewHolder<UserModel> {

        private ImageView ivAvatar, ivAvatarIcon;
        private TextView tvFullName;

        SelectedGroupMemberHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
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
                ivAvatarIcon.setImageResource(R.drawable.ic_verified);
            } else {
                ivAvatarIcon.setVisibility(View.VISIBLE);
                ivAvatarIcon.setImageResource(R.drawable.ic_close_red_circle);
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