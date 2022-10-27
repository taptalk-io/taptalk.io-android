package io.moselo.SampleApps.Fragment;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.View.Fragment.TapBaseChatRoomCustomNavigationBarFragment;
import io.taptalk.TapTalkSample.R;

public class MeetTalkChatRoomNavigationBarFragment extends TapBaseChatRoomCustomNavigationBarFragment {

    private ConstraintLayout clRoomStatus;
    private ConstraintLayout clRoomOnlineStatus;
    private ConstraintLayout clRoomTypingStatus;
    private CircleImageView civRoomImage;
    private ImageView ivButtonBack;
    private ImageView ivRoomIcon;
    private ImageView ivRoomTypingIndicator;
    private ImageView ivButtonVoiceCall;
    private ImageView ivButtonVideoCall;
    private TextView tvRoomName;
    private TextView tvRoomStatus;
    private TextView tvRoomImageLabel;
    private TextView tvRoomTypingStatus;
    private View vRoomImage;
    private View vStatusBadge;

    private RequestManager glide;
    private Handler lastActivityHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meettalk_chat_room_navigation_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        glide = Glide.with(this);
        bindViews(view);
        setupNavigationData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLastActivityHandler().removeCallbacks(lastActivityRunnable); // Stop offline timer
    }

    @Override
    public void onReceiveUpdatedChatRoomData(TAPRoomModel room, @Nullable TAPUserModel recipientUser) {
        setRoomName();
        setRoomStatus();
        setRoomProfilePicture();
        if (null != recipientUser) {
            setOnlineStatus(TAPOnlineStatusModel.Builder(recipientUser));
            setChatRoomStatus(getOnlineStatus());
        }
    }

    @Override
    public void onReceiveStartTyping(String roomID, TAPUserModel user) {
        setTypingIndicator();
    }

    @Override
    public void onReceiveStopTyping(String roomID, TAPUserModel user) {
        setTypingIndicator();
    }

    @Override
    public void onReceiveOnlineStatus(TAPUserModel user, Boolean isOnline, Long lastActive) {
        setChatRoomStatus(new TAPOnlineStatusModel(user, isOnline, lastActive));
    }

    @Override
    public void onShowMessageSelection(List<TAPMessageModel> selectedMessages) {
        showSelectState();
    }

    @Override
    public void onHideMessageSelection() {
        hideSelectState();
    }

    private void bindViews(View view) {
        clRoomStatus = view.findViewById(R.id.cl_room_status);
        clRoomOnlineStatus = view.findViewById(R.id.cl_room_online_status);
        clRoomTypingStatus = view.findViewById(R.id.cl_room_typing_status);
        civRoomImage = view.findViewById(R.id.civ_room_image);
        vStatusBadge = view.findViewById(R.id.v_room_status_badge);
        ivButtonBack = view.findViewById(R.id.iv_button_back);
        ivRoomIcon = view.findViewById(R.id.iv_room_icon);
        ivRoomTypingIndicator = view.findViewById(R.id.iv_room_typing_indicator);
        ivButtonVideoCall = view.findViewById(R.id.iv_button_video_call);
        ivButtonVoiceCall = view.findViewById(R.id.iv_button_voice_call);
        tvRoomName = view.findViewById(R.id.tv_room_name);
        tvRoomStatus = view.findViewById(R.id.tv_room_status);
        tvRoomImageLabel = view.findViewById(R.id.tv_room_image_label);
        tvRoomTypingStatus = view.findViewById(R.id.tv_room_typing_status);
        vRoomImage = view.findViewById(R.id.v_room_image);
    }

    private void setupNavigationData() {
        setRoomName();
        setRoomStatus();
        setRoomProfilePicture();
        setTypingIndicator();

        ivButtonBack.setOnClickListener((v) -> onBackPressed());
        vRoomImage.setOnClickListener(v -> openRoomProfile());
        ivButtonVoiceCall.setOnClickListener((v) -> {});
        ivButtonVideoCall.setOnClickListener((v) -> {});
    }

    private void setRoomName() {
        if (TAPUtils.isSavedMessagesRoom(getRoom().getRoomID(), getInstanceKey())) {
            tvRoomName.setText(R.string.tap_saved_messages);
        } else if (getRoom().getType() == TYPE_PERSONAL && null != getRecipientUser() &&
                (null == getRecipientUser().getDeleted() || getRecipientUser().getDeleted() <= 0L) &&
                !getRecipientUser().getFullname().isEmpty()) {
            tvRoomName.setText(getRecipientUser().getFullname());
        } else {
            tvRoomName.setText(getRoom().getName());
        }
    }

    private void setRoomStatus() {
        if (getRoom().getType() == TYPE_GROUP && null != getRoom().getParticipants()) {
            // Show number of participants for group room
            tvRoomStatus.setText(String.format(getString(R.string.tap_format_d_group_member_count), getRoom().getParticipants().size()));
        }
    }

    private void setRoomProfilePicture() {
        civRoomImage.post(() -> {
            if (!TapUI.getInstance(getInstanceKey()).isProfileButtonVisible()) {
                civRoomImage.setVisibility(View.GONE);
                vRoomImage.setVisibility(View.GONE);
                tvRoomImageLabel.setVisibility(View.GONE);
            } else if (
                    null != getRoom() &&
                            TYPE_PERSONAL == getRoom().getType() &&
                            null != getRecipientUser() &&
                            (null != getRecipientUser().getDeleted() && getRecipientUser().getDeleted() > 0L)
            ) {
                glide.load(R.drawable.tap_ic_deleted_user).fitCenter().into(civRoomImage);
                ImageViewCompat.setImageTintList(civRoomImage, null);
                tvRoomImageLabel.setVisibility(View.GONE);
                clRoomStatus.setVisibility(View.GONE);
            } else if (
                    null != getRoom() &&
                            TYPE_PERSONAL == getRoom().getType() &&
                            TAPUtils.isSavedMessagesRoom(getRoom().getRoomID(), getInstanceKey())
            ) {
                glide.load(R.drawable.tap_ic_bookmark_round).fitCenter().into(civRoomImage);
                ImageViewCompat.setImageTintList(civRoomImage, null);
                tvRoomImageLabel.setVisibility(View.GONE);
                clRoomStatus.setVisibility(View.GONE);
            } else if (
                    null != getRoom() &&
                            TYPE_PERSONAL == getRoom().getType() &&
                            null != getRecipientUser() &&
                            null != getRecipientUser().getImageURL().getThumbnail() &&
                            !getRecipientUser().getImageURL().getThumbnail().isEmpty()
            ) {
                // Load user avatar URL
                loadProfilePicture(getRecipientUser().getImageURL().getThumbnail(), civRoomImage, tvRoomImageLabel);
                getRoom().setImageURL(getRecipientUser().getImageURL());
            } else if (
                    null != getRoom() &&
                            !getRoom().isDeleted() &&
                            null != getRoom().getImageURL() &&
                            !getRoom().getImageURL().getThumbnail().isEmpty()
            ) {
                // Load room image
                loadProfilePicture(getRoom().getImageURL().getThumbnail(), civRoomImage, tvRoomImageLabel);
            } else {
                loadInitialsToProfilePicture(civRoomImage, tvRoomImageLabel);
            }
        });

        if (null != getRecipientUser() &&
                null != getRecipientUser().getUserRole() &&
                null != getRecipientUser().getUserRole().getIconURL() &&
                null != getRoom() &&
                TYPE_PERSONAL == getRoom().getType() &&
                !getRecipientUser().getUserRole().getIconURL().isEmpty()
        ) {
            glide.load(getRecipientUser().getUserRole().getIconURL()).into(ivRoomIcon);
            ivRoomIcon.setVisibility(View.VISIBLE);
        } else {
            ivRoomIcon.setVisibility(View.GONE);
        }
    }

    private void loadProfilePicture(String image, ImageView imageView, TextView tvAvatarLabel) {
        if (imageView.getVisibility() == View.GONE) {
            return;
        }
        glide.load(image).listener(new RequestListener<>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (null != getActivity()) {
                    getActivity().runOnUiThread(() -> loadInitialsToProfilePicture(imageView, tvAvatarLabel));
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                ImageViewCompat.setImageTintList(imageView, null);
                tvAvatarLabel.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
    }

    private void loadInitialsToProfilePicture(ImageView imageView, TextView tvAvatarLabel) {
        if (imageView.getVisibility() == View.GONE || null == getContext()) {
            return;
        }
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(TAPUtils.getRandomColor(getContext(), getRoom().getName())));
        tvAvatarLabel.setText(TAPUtils.getInitials(getRoom().getName(), getRoom().getType() == TYPE_PERSONAL ? 2 : 1));
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.tap_bg_circle_9b9b9b));
        tvAvatarLabel.setVisibility(View.VISIBLE);
    }

    private void setChatRoomStatus(TAPOnlineStatusModel onlineStatus) {
        setOnlineStatus(onlineStatus);
        if (onlineStatus.getUser().getUserID().equals(getRecipientUser().getUserID()) && onlineStatus.getOnline()) {
            // User is online
            showUserOnline();
        } else if (onlineStatus.getUser().getUserID().equals(getRecipientUser().getUserID()) && !onlineStatus.getOnline()) {
            // User is offline
            showUserOffline();
        }
    }

    private Handler getLastActivityHandler() {
        return null == lastActivityHandler ? lastActivityHandler = new Handler() : lastActivityHandler;
    }

    private void showUserOnline() {
        if (null == getActivity()) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            if (0 >= getTypingUsers().size()) {
                clRoomStatus.setVisibility(View.VISIBLE);
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
            vStatusBadge.setVisibility(View.VISIBLE);
            vStatusBadge.setBackground(ContextCompat.getDrawable(getActivity(), io.taptalk.TapTalk.R.drawable.tap_bg_circle_active));
            tvRoomStatus.setText(getString(io.taptalk.TapTalk.R.string.tap_active_now));
            getLastActivityHandler().removeCallbacks(lastActivityRunnable);
        });
    }

    private void showUserOffline() {
        if (null == getActivity()) {
            return;
        }
        getActivity().runOnUiThread(() -> {
            if (0 >= getTypingUsers().size()) {
                clRoomStatus.setVisibility(View.VISIBLE);
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
            lastActivityRunnable.run();
        });
    }

    private Runnable lastActivityRunnable = new Runnable() {
        final int INTERVAL = 1000 * 60;

        @Override
        public void run() {
            if (null == getActivity()) {
                return;
            }
            Long lastActive = getOnlineStatus().getLastActive();
            if (lastActive == 0) {
                getActivity().runOnUiThread(() -> {
                    vStatusBadge.setVisibility(View.VISIBLE);
                    vStatusBadge.setBackground(null);
                    tvRoomStatus.setText("");
                });
            } else {
                getActivity().runOnUiThread(() -> {
                    vStatusBadge.setVisibility(View.GONE);
                    tvRoomStatus.setText(TAPTimeFormatter.getLastActivityString(getActivity(), lastActive));
                });
            }
            getLastActivityHandler().postDelayed(this, INTERVAL);
        }
    };

    private void setTypingIndicator() {
        if (getTypingUsers().size() > 0) {
            showTypingIndicator();
        }
        else {
            hideTypingIndicator();
        }
    }

    private void showTypingIndicator() {
        if (null == getActivity()) {
            return;
        }
        typingIndicatorTimeoutTimer.cancel();
        typingIndicatorTimeoutTimer.start();
        getActivity().runOnUiThread(() -> {
            clRoomStatus.setVisibility(View.VISIBLE);
            clRoomTypingStatus.setVisibility(View.VISIBLE);
            clRoomOnlineStatus.setVisibility(View.GONE);

            if (TYPE_PERSONAL == getRoom().getType()) {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                tvRoomTypingStatus.setText(getString(R.string.tap_typing));
            } else if (1 < getTypingUsers().size()) {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                tvRoomTypingStatus.setText(String.format(getString(R.string.tap_format_d_people_typing), getTypingUsers().size()));
            } else {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                String firstTypingUserName = "";
                if (getTypingUsers().size() > 0) {
                    TAPUserModel firstTypingUser = getTypingUsers().entrySet().iterator().next().getValue();
                    firstTypingUserName =  firstTypingUser.getFullname().split(" ")[0];
                }
                tvRoomTypingStatus.setText(String.format(getString(R.string.tap_format_s_typing_single), firstTypingUserName));
            }
        });
    }

    private void hideTypingIndicator() {
        if (null == getActivity()) {
            return;
        }
        typingIndicatorTimeoutTimer.cancel();
        getActivity().runOnUiThread(() -> {
            getTypingUsers().clear();
            clRoomStatus.setVisibility(View.VISIBLE);
            clRoomTypingStatus.setVisibility(View.GONE);
            clRoomOnlineStatus.setVisibility(View.VISIBLE);
        });
    }

    private final CountDownTimer typingIndicatorTimeoutTimer = new CountDownTimer(TYPING_INDICATOR_TIMEOUT, 1000L) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            hideTypingIndicator();
        }
    };

    private void showSelectState() {
        vRoomImage.setEnabled(false);
        if (null != getContext()) {
            ivButtonBack.setImageDrawable(ContextCompat.getDrawable(getContext(), io.taptalk.TapTalk.R.drawable.tap_ic_close_grey));
        }
    }

    private void hideSelectState() {
        vRoomImage.setEnabled(true);
        if (null != getContext()) {
            ivButtonBack.setImageDrawable(ContextCompat.getDrawable(getContext(), io.taptalk.TapTalk.R.drawable.tap_ic_chevron_left_white));
        }
    }

    private void openRoomProfile() {
        if (null == getActivity()) {
            return;
        }
        TAPChatManager.getInstance(getInstanceKey()).triggerChatRoomProfileButtonTapped(getActivity(), getRoom(), getRecipientUser());
        if (getActivity() instanceof TapUIChatActivity) {
            ((TapUIChatActivity) getActivity()).hideUnreadButton();
        }
    }
}
