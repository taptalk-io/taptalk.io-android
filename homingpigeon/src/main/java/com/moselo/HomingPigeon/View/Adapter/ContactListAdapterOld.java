package com.moselo.HomingPigeon.View.Adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.ContactListListener;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

@Deprecated
public class ContactListAdapterOld extends RecyclerView.Adapter<ContactListAdapterOld.ContactListHolder> {

    private List<UserModel> contactList;
    private ContactListListener listener;
    private ColorStateList avatarTint;
    private int viewType;

    public static final int NONE = 0;
    public static final int CHAT = 1;
    public static final int SELECT = 2;
    public static final int SELECTED_MEMBER = 3;

    public ContactListAdapterOld(int viewType, List<UserModel> contactList) {
        this.viewType = viewType;
        this.contactList = contactList;
    }

    public ContactListAdapterOld(int viewType, List<UserModel> contactList, @Nullable ContactListListener listener) {
        this.viewType = viewType;
        this.contactList = contactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case SELECTED_MEMBER:
                return new ContactListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_group_member, parent, false));
            default:
                return new ContactListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_user_contact, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (null != contactList) return contactList.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    public List<UserModel> getItems() {
        return contactList;
    }

    public UserModel getItemAt(int position) {
        return contactList.get(position);
    }

    class ContactListHolder extends RecyclerView.ViewHolder {

        private ImageView ivAvatar, ivAvatarIcon, ivSelection;
        private TextView tvFullName;
        private View vSeparator;
        private UserModel item;

        ContactListHolder(View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivAvatarIcon = itemView.findViewById(R.id.iv_avatar_icon);
            ivSelection = itemView.findViewById(R.id.iv_selection);
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
                        item.setSelected(!item.isSelected());
                        listener.onContactSelected(item, item.isSelected());
                        notifyItemChanged(position);
                        break;
                }
            });
        }
    }

    class SelectedGroupMemberHolder extends RecyclerView.ViewHolder {

        public SelectedGroupMemberHolder(View itemView) {
            super(itemView);
        }
    }
}