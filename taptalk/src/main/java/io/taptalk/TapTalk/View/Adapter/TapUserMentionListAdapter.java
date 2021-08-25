package io.taptalk.TapTalk.View.Adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPBaseViewHolder;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.R;


public class TapUserMentionListAdapter extends TAPBaseAdapter<TAPUserModel, TAPBaseViewHolder<TAPUserModel>> {

    private TapUserMentionListInterface listener;

    public TapUserMentionListAdapter(List<TAPUserModel> contactList, TapUserMentionListInterface listener) {
        setItems(contactList, false);
        this.listener = listener;
    }

    public interface TapUserMentionListInterface {
        void onUserTapped(TAPUserModel user);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public TAPBaseViewHolder<TAPUserModel> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContactListViewHolder(parent, R.layout.tap_cell_user_mention_list);
    }

    class ContactListViewHolder extends TAPBaseViewHolder<TAPUserModel> {

        private CircleImageView civAvatar;
        private TextView tvAvatarLabel, tvFullName, tvUsername;
        private View vSeparator;

        ContactListViewHolder(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);

            civAvatar = itemView.findViewById(R.id.civ_avatar);
            tvAvatarLabel = itemView.findViewById(R.id.tv_avatar_label);
            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvUsername = itemView.findViewById(R.id.tv_username);
            vSeparator = itemView.findViewById(R.id.v_separator);
        }

        @Override
        protected void onBind(TAPUserModel item, int position) {
            if (null != item.getImageURL() && !item.getImageURL().getThumbnail().isEmpty()) {
                // Load profile picture
                Glide.with(itemView.getContext())
                        .load(item.getImageURL().getThumbnail())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // Show initial
                                if (itemView.getContext() instanceof Activity) {
                                    ((Activity) itemView.getContext()).runOnUiThread(() -> {
                                        ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.getContext(), item.getFullname())));
                                        civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
                                        tvAvatarLabel.setText(TAPUtils.getInitials(item.getFullname(), 2));
                                        tvAvatarLabel.setVisibility(View.VISIBLE);
                                    });
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, null);
                tvAvatarLabel.setVisibility(View.GONE);
            } else {
                // Show initial
                Glide.with(itemView.getContext()).clear(civAvatar);
                ImageViewCompat.setImageTintList(civAvatar, ColorStateList.valueOf(TAPUtils.getRandomColor(itemView.getContext(), item.getFullname())));
                civAvatar.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.tap_bg_circle_9b9b9b));
                tvAvatarLabel.setText(TAPUtils.getInitials(item.getFullname(), 2));
                tvAvatarLabel.setVisibility(View.VISIBLE);
            }

            // Set name and username
            tvFullName.setText(item.getFullname());
            if (null != item.getUsername() && !item.getUsername().isEmpty()) {
                tvUsername.setText(String.format("@%s", item.getUsername()));
                tvUsername.setVisibility(View.VISIBLE);
            } else {
                tvUsername.setVisibility(View.GONE);
            }

            // Remove separator on last item
            if (position == getItemCount() - 1) {
                vSeparator.setVisibility(View.GONE);
            } else {
                vSeparator.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> listener.onUserTapped(item));
        }
    }

}
