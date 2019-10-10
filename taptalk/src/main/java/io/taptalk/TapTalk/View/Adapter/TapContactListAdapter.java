package io.taptalk.TapTalk.View.Adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
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
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapContactListListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.MENU_ID_CREATE_NEW_GROUP;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.MENU_ID_ADD_NEW_CONTACT;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.MENU_ID_SCAN_QR_CODE;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_DEFAULT_CONTACT_LIST;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_INFO_LABEL;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_MENU_BUTTON;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_SECTION_TITLE;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_SELECTABLE_CONTACT_LIST;
import static io.taptalk.TapTalk.Model.ResponseModel.TapContactListModel.TYPE_SELECTED_GROUP_MEMBER;

public class TapContactListAdapter extends TAPBaseAdapter<TapContactListModel, TAPBaseViewHolder<TapContactListModel>> {

    private TapContactListListener listener;
    private String myID;
    private boolean isAnimating = true;

    public TapContactListAdapter(List<TapContactListModel> contactList) {
        setItems(contactList, false);
        this.myID = TAPChatManager.getInstance().getActiveUser().getUserID();
    }

    public TapContactListAdapter(List<TapContactListModel> contactList, TapContactListListener listener) {
        setItems(contactList, false);
        this.listener = listener;
        this.myID = TAPChatManager.getInstance().getActiveUser().getUserID();
    }

    // Constructor for selectable contacts
//    public TapContactListAdapter(List<TAPUserModel> contactList, List<TAPUserModel> selectedContacts, @Nullable TapContactListListener listener) {
//        setItems(contactList, false);
//        this.viewType = SELECT;
//        this.selectedContacts = selectedContacts;
//        this.myID = TAPChatManager.getInstance().getActiveUser().getUserID();
//        this.listener = listener;
//    }

    @Override
    public int getItemViewType(int position) {
        return getItemAt(position).getType();
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TapContactListModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SELECTED_GROUP_MEMBER:
                return new SelectedGroupMemberViewHolder(parent, R.layout.tap_cell_group_member);
            case TYPE_SECTION_TITLE:
                return new SectionTitleViewHolder(parent, R.layout.tap_cell_section_title);
            case TYPE_MENU_BUTTON:
                return new MenuButtonViewHolder(parent, R.layout.tap_cell_new_chat_menu_button);
            case TYPE_INFO_LABEL:
                return new InfoLabelViewHolder(parent, R.layout.tap_cell_info_label);
            case TYPE_DEFAULT_CONTACT_LIST:
            case TYPE_SELECTABLE_CONTACT_LIST:
            default:
                return new ContactListViewHolder(parent, R.layout.tap_cell_user_contact);
        }
    }

    class ContactListViewHolder extends TAPBaseViewHolder<TapContactListModel> {

        private CircleImageView civAvatar;
        private ImageView ivAvatarIcon, ivSelection;
        private TextView tvAvatarLabel, tvFullName, tvUsername;
        private View vSeparator;

        ContactListViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            civAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivSelection = itemView.findViewById(R.id.iv_selection);
            tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvUsername = itemView.findViewById(R.id.tv_username);
            vSeparator = itemView.findViewById(R.id.v_separator);
        }

        @Override
        protected void onBind(TapContactListModel item, int position) {
            TAPUserModel user = item.getUser();
            if (null == user) {
                return;
            }

            if (null != user.getAvatarURL() && !user.getAvatarURL().getThumbnail().isEmpty()) {
                // Load profile picture
                Glide.with(itemView.getContext())
                        .load(user.getAvatarURL().getThumbnail())
                        .apply(new RequestOptions().centerCrop())
                        .into(civAvatar);
                civAvatar.setImageTintList(null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else {
                // Set initial as avatar
                civAvatar.setImageTintList(ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(user.getName())));
                civAvatar.setImageResource(R.drawable.tap_bg_circle_9b9b9b);
                tvAvatarLabel.setText(TAPUtils.getInstance().getInitials(user.getName(), 2));
                tvAvatarLabel.setVisibility(View.VISIBLE);
            }

            // Change avatar icon and background
            // TODO: 7 September 2018 SET AVATAR ICON ACCORDING TO USER ROLE

            // Set name
            tvFullName.setText(user.getName());

            // Remove separator on last item
            if (position == getItemCount() - 1 || getItemAt(position + 1).getType() != item.getType()) {
                vSeparator.setVisibility(View.GONE);
            } else {
                vSeparator.setVisibility(View.VISIBLE);
            }

            // Show/hide selection
            if (item.getType() == TYPE_SELECTABLE_CONTACT_LIST && item.isSelected()) {
                ivSelection.setImageResource(R.drawable.tap_ic_circle_active);
                ivSelection.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconCircleSelectionActive)));
                tvUsername.setText(String.format("@%s", user.getUsername()));
                ivSelection.setVisibility(View.VISIBLE);
                tvUsername.setVisibility(View.VISIBLE);
            } else if (item.getType() == TYPE_SELECTABLE_CONTACT_LIST) {
                ivSelection.setImageResource(R.drawable.tap_ic_circle_inactive);
                ivSelection.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconCircleSelectionInactive)));
                tvUsername.setText(String.format("@%s", user.getUsername()));
                ivSelection.setVisibility(View.VISIBLE);
                tvUsername.setVisibility(View.VISIBLE);
            } else {
                ivSelection.setVisibility(View.GONE);
                tvUsername.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> onContactClicked(item, position));
        }

        private void onContactClicked(TapContactListModel item, int position) {
            TAPUserModel user = item.getUser();
            if (null == user) {
                return;
            }
            switch (item.getType()) {
                case TYPE_DEFAULT_CONTACT_LIST:
                    if (null != listener) {
                        listener.onContactTapped(item);
                    }
                    break;
                case TYPE_SELECTABLE_CONTACT_LIST:
                    item.setSelected(!item.isSelected());
                    if (user.getUserID().equals(myID)) {
                        return;
                    } else if (null != listener) {
                        if (item.isSelected()) {
                            listener.onContactSelected(item);
                        } else {
                            listener.onContactDeselected(item);
                        }
                        isAnimating = true;
                        notifyItemChanged(position);
                    }
                    break;
            }
        }
    }

    class SelectedGroupMemberViewHolder extends TAPBaseViewHolder<TapContactListModel> {

        private CircleImageView civAvatar;
        private ImageView ivAvatarIcon;
        private TextView tvAvatarLabel, tvFullName;

        SelectedGroupMemberViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            civAvatar = itemView.findViewById(R.id.civ_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
        }

        @Override
        protected void onBind(TapContactListModel item, int position) {
            TAPUserModel user = item.getUser();
            if (null == user) {
                return;
            }
            if (null != user.getAvatarURL() && !user.getAvatarURL().getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(user.getAvatarURL().getThumbnail())
                        .apply(new RequestOptions().centerCrop())
                        .into(civAvatar);
                civAvatar.setImageTintList(null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else {
                civAvatar.setImageTintList(ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(user.getName())));
                civAvatar.setImageResource(R.drawable.tap_bg_circle_9b9b9b);
                tvAvatarLabel.setText(TAPUtils.getInstance().getInitials(user.getName(), 2));
                tvAvatarLabel.setVisibility(View.VISIBLE);
            }

            // Set name
            String fullName = user.getName();
            if (user.getUserID().equals(myID)) {
                tvFullName.setText(R.string.tap_you);
            } else if (fullName.contains(" ")) {
                tvFullName.setText(fullName.substring(0, fullName.indexOf(' ')));
            } else {
                tvFullName.setText(fullName);
            }

            // Update avatar icon
            if ((null == listener || user.getUserID().equals(myID)) /*&& item.getUserRole().equals("1")*/) {
                ivAvatarIcon.setVisibility(View.GONE);
//            } else if ((null == listener || item.getUserID().equals(myID)) /*&& item.getUserRole().equals("2")*/) {
//                ivAvatarIcon.setVisibility(View.VISIBLE);
//                ivAvatarIcon.setImageResource(R.drawable.tap_ic_verified);
//                ivAvatarIcon.setBackground(null);
            } else {
                ivAvatarIcon.setImageResource(R.drawable.tap_ic_remove_red_circle_background);
                ivAvatarIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconRemoveItemBackground)));
                ivAvatarIcon.setBackgroundResource(R.drawable.tap_bg_circle_remove_item);
                ivAvatarIcon.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> deselectContact(item));
        }

        private void deselectContact(TapContactListModel item) {
            TAPUserModel user = item.getUser();
            if (null == user) {
                return;
            }
            if (null != listener && !user.getUserID().equals(myID) && !isAnimating) {
                isAnimating = true;
                listener.onContactDeselected(item);
            }
        }
    }

    public class SectionTitleViewHolder extends TAPBaseViewHolder<TapContactListModel> {

        private TextView tvRecentTitle;

        SectionTitleViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvRecentTitle = itemView.findViewById(R.id.tv_section_title);
        }

        @Override
        protected void onBind(TapContactListModel item, int position) {
            tvRecentTitle.setText(item.getTitle());
        }
    }

    public class MenuButtonViewHolder extends TAPBaseViewHolder<TapContactListModel> {

        private ConstraintLayout clContainer;
        private ImageView ivMenuIcon;
        private TextView tvMenuLabel;

        MenuButtonViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            clContainer = itemView.findViewById(R.id.cl_container);
            ivMenuIcon = itemView.findViewById(R.id.iv_menu_icon);
            tvMenuLabel = itemView.findViewById(R.id.tv_menu_label);
        }

        @Override
        protected void onBind(TapContactListModel item, int position) {
            switch (item.getActionId()) {
                case MENU_ID_ADD_NEW_CONTACT:
                    ivMenuIcon.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconMenuNewContact));
                    break;
                case MENU_ID_SCAN_QR_CODE:
                    ivMenuIcon.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconMenuScanQRCode));
                    break;
                case MENU_ID_CREATE_NEW_GROUP:
                    ivMenuIcon.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconMenuCreateNewGroup));
                    break;
                default:
                    ivMenuIcon.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapColorPrimaryIcon));
                    break;
            }
            ivMenuIcon.setImageResource(item.getDrawableResource());
            tvMenuLabel.setText(item.getButtonText());

            clContainer.setOnClickListener(v -> {
                if (null != listener) {
                    listener.onMenuButtonTapped(item.getActionId());
                }
            });
        }
    }

    public class InfoLabelViewHolder extends TAPBaseViewHolder<TapContactListModel> {

        TextView tvInfoLabel, tvInfoActionButton;

        InfoLabelViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
            tvInfoLabel = itemView.findViewById(R.id.tv_info_label);
            tvInfoActionButton = itemView.findViewById(R.id.tv_info_action_button);
        }

        @Override
        protected void onBind(TapContactListModel item, int position) {
            tvInfoLabel.setText(item.getTitle());
            tvInfoActionButton.setText(item.getButtonText());

            tvInfoActionButton.setOnClickListener(v -> {
                if (null != listener) {
                    TAPUtils.getInstance().animateClickButton(tvInfoActionButton, 0.95f);
                    listener.onInfoLabelButtonTapped(item.getActionId());
                }
            });
        }
    }
}
