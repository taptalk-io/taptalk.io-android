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

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.HpBaseViewHolder;
import com.moselo.HomingPigeon.Helper.GlideApp;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Interface.ContactListInterface;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.RoomModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.HpChatActivity;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.Extras.ROOM_NAME;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_COLOR;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_MY_USERNAME;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_ROOM;
import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_USER;

public class HpContactListAdapter extends HpBaseAdapter<UserModel, HpBaseViewHolder<UserModel>> {

    private ContactListInterface listener;
    private ColorStateList avatarTint;
    private String myID;
    private int viewType;
    private boolean isAnimating = true;

    public static final int NONE = 0;
    public static final int CHAT = 1;
    public static final int SELECT = 2;
    public static final int SELECTED_MEMBER = 3;

    public HpContactListAdapter(int viewType, List<UserModel> contactList) {
        setItems(contactList, false);
        this.viewType = viewType;
    }

    public HpContactListAdapter(int viewType, List<UserModel> contactList, @Nullable ContactListInterface listener, String myID) {
        setItems(contactList, false);
        this.viewType = viewType;
        this.listener = listener;
        this.myID = myID;
    }

    @NonNull
    @Override
    public HpBaseViewHolder<UserModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

    class ContactListHolder extends HpBaseViewHolder<UserModel> {

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
            final int randomColor = HpUtils.getInstance().getRandomColor(item.getName());

            // TODO: 6 September 2018 LOAD AVATAR IMAGE TO VIEW
            avatarTint = ColorStateList.valueOf(randomColor);
            ivAvatar.setBackgroundTintList(avatarTint);

            // Change avatar icon and background
            // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE
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
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
                        UserModel myUser = HpUtils.getInstance().fromJSON(new TypeReference<UserModel>() {
                        }, prefs.getString(K_USER, ""));

                        // TODO: 17 September 2018 OPEN CHAT ROOM
                        if (!myID.equals(item.getUserID())) {
                            HpChatManager.getInstance().saveUnsentMessage();
                            Intent intent = new Intent(itemView.getContext(), HpChatActivity.class);
                            intent.putExtra(K_MY_USERNAME, myUser.getName());
                            intent.putExtra(ROOM_NAME, item.getName());
                            intent.putExtra(K_COLOR, randomColor);
                            intent.putExtra(K_ROOM, RoomModel.Builder(HpChatManager.getInstance().arrangeRoomId(myID, item.getUserID()),
                                    item.getName(), 1));
                            itemView.getContext().startActivity(intent);

                            HpDataManager.getInstance().saveRecipientID(itemView.getContext(), item.getUserID());
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

    class SelectedGroupMemberHolder extends HpBaseViewHolder<UserModel> {

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
            final int randomColor = HpUtils.getInstance().getRandomColor(item.getName());

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