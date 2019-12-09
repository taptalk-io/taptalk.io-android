package io.taptalk.TapTalk.View.Adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
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

public class TapSelectedGroupMemberAdapter extends TAPBaseAdapter<TapContactListModel, TAPBaseViewHolder<TapContactListModel>> {

    private TapContactListListener listener;
    private String myID;
    private boolean isAnimating = true;

    public TapSelectedGroupMemberAdapter(List<TapContactListModel> contactList) {
        setItems(contactList, false);
        this.myID = TAPChatManager.getInstance().getActiveUser().getUserID();
    }

    public TapSelectedGroupMemberAdapter(List<TapContactListModel> contactList, TapContactListListener listener) {
        setItems(contactList, false);
        this.listener = listener;
        this.myID = TAPChatManager.getInstance().getActiveUser().getUserID();
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TapContactListModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectedGroupMemberViewHolder(parent, R.layout.tap_cell_group_member);
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
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else {
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getInstance().getRandomColor(user.getName())));
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
                ImageViewCompat.setImageTintList(ivAvatarIcon, ColorStateList.valueOf(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconRemoveItemBackground)));
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
            item.setSelected(false);
            if (null != listener && !user.getUserID().equals(myID) && !isAnimating) {
                isAnimating = true;
                listener.onContactDeselected(item);
            }
        }
    }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }
}
