package com.moselo.HomingPigeon.View.Adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class SubContactListAdapter extends RecyclerView.Adapter<SubContactListAdapter.SubContactListHolder> {

    private List<UserModel> subContactList;
    private ColorStateList avatarTint;

    public SubContactListAdapter(List<UserModel> subContactList) {
        this.subContactList = subContactList;
    }

    @NonNull
    @Override
    public SubContactListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubContactListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_user_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubContactListHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (null != subContactList) return subContactList.size();
        return 0;
    }

    public List<UserModel> getItems() {
        return subContactList;
    }

    public UserModel getItemAt(int position) {
        return subContactList.get(position);
    }

    class SubContactListHolder extends RecyclerView.ViewHolder {

        private ImageView ivAvatar, ivAvatarIcon;
        private TextView tvFullName;
        private View vSeparator;
        private UserModel item;

        SubContactListHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            vSeparator = itemView.findViewById(R.id.v_separator);
        }

        void onBind(int position) {
            item = getItemAt(position);
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
            }

            itemView.setOnClickListener(v -> {
                // TODO: 13 September 2018 OPEN CHAT ROOM
            });
        }
    }
}