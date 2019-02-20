package io.taptalk.TapTalk.View.Adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkContactListInterface;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.Taptalk.R;

public class TAPContactListAdapter extends TAPBaseAdapter<TAPUserModel, TAPBaseViewHolder<TAPUserModel>> {

    private TapTalkContactListInterface listener;
    private List<TAPUserModel> selectedContacts;
    private String myID;
    private int viewType;
    private boolean isAnimating = true;

    public static final int NONE = 0;
    public static final int CHAT = 1;
    public static final int SELECT = 2;
    public static final int SELECTED_MEMBER = 3;

    public TAPContactListAdapter(int viewType, List<TAPUserModel> contactList) {
        setItems(contactList, false);
        this.viewType = viewType;
        this.myID = TAPChatManager.getInstance().getActiveUser().getUserID();
    }

    public TAPContactListAdapter(int viewType, List<TAPUserModel> contactList, @Nullable TapTalkContactListInterface listener) {
        setItems(contactList, false);
        this.viewType = viewType;
        this.listener = listener;
        this.myID = TAPChatManager.getInstance().getActiveUser().getUserID();
    }

    // Constructor for selectable contacts
    public TAPContactListAdapter(List<TAPUserModel> contactList, List<TAPUserModel> selectedContacts, @Nullable TapTalkContactListInterface listener) {
        setItems(contactList, false);
        this.viewType = SELECT;
        this.selectedContacts = selectedContacts;
        this.myID = TAPChatManager.getInstance().getActiveUser().getUserID();
        this.listener = listener;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPUserModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case SELECTED_MEMBER:
                return new SelectedGroupMemberHolder(parent, R.layout.tap_cell_group_member);
            default:
                return new ContactListHolder(parent, R.layout.tap_cell_user_contact);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    class ContactListHolder extends TAPBaseViewHolder<TAPUserModel> {

        private CircleImageView civAvatar;
        private ImageView ivAvatarIcon, ivSelection;
        private TextView tvFullName;
        private View vSeparator;

        ContactListHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            civAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivSelection = itemView.findViewById(R.id.iv_selection);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            vSeparator = itemView.findViewById(R.id.v_separator);
        }

        @Override
        protected void onBind(TAPUserModel item, int position) {
            if (null != item.getAvatarURL() && !item.getAvatarURL().getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getAvatarURL().getThumbnail())
                        .apply(new RequestOptions().centerCrop())
                        .into(civAvatar);
            } else {
                civAvatar.setImageDrawable(itemView.getContext().getDrawable(R.drawable.tap_img_default_avatar));
            }

            // Change avatar icon and background
            // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE
            //if (null != item.getAvatarURL())
            //  Glide.with(itemView.getContext()).load(item.getAvatarURL().getThumbnail()).apply(new RequestOptions().centerCrop()).into(ivAvatar);

            // Set name
            tvFullName.setText(item.getName());

            // Remove separator on last item
            if (position == getItemCount() - 1) {
                vSeparator.setVisibility(View.GONE);
            } else {
                vSeparator.setVisibility(View.VISIBLE);
            }

            // Show/hide selection
            if (viewType == SELECT && selectedContacts.contains(item)) {
                ivSelection.setVisibility(View.VISIBLE);
                ivSelection.setImageResource(R.drawable.tap_ic_circle_active);
            } else if (viewType == SELECT && !selectedContacts.contains(item)) {
                ivSelection.setVisibility(View.VISIBLE);
                ivSelection.setImageResource(R.drawable.tap_ic_circle_inactive);
            } else {
                ivSelection.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> onContactClicked(item, position));
        }

        private void onContactClicked(TAPUserModel item, int position) {
            switch (viewType) {
                case CHAT:
                    if (!myID.equals(item.getUserID())) {
                        // Save user data to contact manager
                        //TAPContactManager.getInstance().updateUserDataMap(item);

                        // TODO: 25 October 2018 SET ROOM TYPE AND COLOR
                        TAPUtils.getInstance().startChatActivity(
                                itemView.getContext(),
                                TAPChatManager.getInstance().arrangeRoomId(myID, item.getUserID()),
                                item.getName(),
                                item.getAvatarURL(),
                                1,
                                /* TEMPORARY ROOM COLOR */TAPUtils.getInstance().getRandomColor(item.getName()) + "");
                    }
                    break;
                case SELECT:
                    if (item.getUserID().equals(myID)) {
                        return;
                    } else if (null != listener && listener.onContactSelected(item)) {
                        isAnimating = true;
                        notifyItemChanged(position);
                    }
                    break;
            }
        }
    }

    class SelectedGroupMemberHolder extends TAPBaseViewHolder<TAPUserModel> {

        private CircleImageView ivAvatar;
        private ImageView ivAvatarIcon;
        private TextView tvFullName;

        SelectedGroupMemberHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            ivAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
        }

        @Override
        protected void onBind(TAPUserModel item, int position) {
            if (null != item.getAvatarURL() && !item.getAvatarURL().getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext()).load(item.getAvatarURL().getThumbnail()).into(ivAvatar);
                ivAvatar.setBackground(null);
            } else {
                ivAvatar.setImageDrawable(null);
                ivAvatar.setBackground(itemView.getContext().getDrawable(R.drawable.tap_bg_circle_9b9b9b));
                ivAvatar.setBackgroundTintList(ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(item.getName())));
            }

            // Set name
            String fullName = item.getName();
            if (item.getUserID().equals(myID)) {
                tvFullName.setText(R.string.tap_you);
            } else if (fullName.contains(" ")) {
                tvFullName.setText(fullName.substring(0, fullName.indexOf(' ')));
            } else tvFullName.setText(fullName);

            // TODO: 19 September 2018 UPDATE EXPERT ICON
            // Update avatar icon
            if ((null == listener || item.getUserID().equals(myID)) /*&& item.getUserRole().equals("1")*/) {
                ivAvatarIcon.setVisibility(View.GONE);
            } else if ((null == listener || item.getUserID().equals(myID)) /*&& item.getUserRole().equals("2")*/) {
                ivAvatarIcon.setVisibility(View.VISIBLE);
                ivAvatarIcon.setImageResource(R.drawable.tap_ic_verified);
            } else {
                ivAvatarIcon.setVisibility(View.VISIBLE);
                ivAvatarIcon.setImageResource(R.drawable.tap_ic_close_red_circle);
            }

            itemView.setOnClickListener(v -> deselectContact(item));
        }

        private void deselectContact(TAPUserModel item) {
            if (null != listener && !item.getUserID().equals(myID) && !isAnimating) {
                isAnimating = true;
                listener.onContactDeselected(item);
            }
        }
    }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }
}