package io.taptalk.TapTalk.View.Fragment;

import android.app.Activity;
import androidx.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.AnalyticsManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPRoomListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;
import io.taptalk.Taptalk.BuildConfig;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST_BADGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.REFRESH_TOKEN_RENEWED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_PROFILE_PICTURE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.LEAVE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;

public class TapUIRoomListFragment extends Fragment {

    private String TAG = TapUIRoomListFragment.class.getSimpleName();
    private TapUIMainRoomListFragment mainRoomListFragment;

    private Activity activity;

    private ConstraintLayout clActionBar, clButtonSearch, clSelection;
    private FrameLayout flSetupContainer;
    private LinearLayout llRoomEmpty, llRetrySetup;
    private TextView tvSelectionCount, tvMyAvatarLabel, tvStartNewChatDescription, tvStartNewChat, tvSetupChat, tvSetupChatDescription;
    private ImageView ivButtonClose, ivButtonNewChat, ivButtonCancelSelection, ivButtonMute, ivButtonDelete, ivButtonMore, ivSetupChat, ivSetupChatLoading;
    private CircleImageView civMyAvatarImage;
    private CardView cvButtonSearch;
    private View vButtonMyAccount;

    private RecyclerView rvContactList;
    private LinearLayoutManager llm;
    private TAPRoomListAdapter adapter;
    private TapTalkRoomListInterface tapTalkRoomListInterface;
    private TAPRoomListViewModel vm;
    private HashMap<String, CountDownTimer> typingIndicatorTimeoutTimers;

    private TAPChatListener chatListener;

    private TapTalkNetworkInterface networkListener = () -> {
        if (vm.isDoneFirstSetup()) {
            updateQueryRoomListFromBackground();
        }
    };

    public TapUIRoomListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainRoomListFragment = (TapUIMainRoomListFragment) this.getParentFragment();
        return inflater.inflate(R.layout.tap_fragment_room_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initListener();
        initView(view);
        viewLoadedSequence();
        TAPBroadcastManager.register(activity, reloadRoomListReceiver, RELOAD_ROOM_LIST, CLEAR_ROOM_LIST_BADGE);
        TAPBroadcastManager.register(activity, reloadProfilePictureReceiver, RELOAD_PROFILE_PICTURE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TAPGroupManager.Companion.getGetInstance().getRefreshRoomList()) {
            runFullRefreshSequence();
        }
        // TODO: 29 October 2018 UPDATE UNREAD BADGE
        TAPNotificationManager.getInstance().setRoomListAppear(true);
        new Thread(() -> TAPChatManager.getInstance().saveMessageToDatabase()).start();
        Log.e(TAG, "onResume: updateQueryRoomListFromBackground");
        // TODO: 18 Feb 2020 DATABASE FIRST QUERY CALLED TWICE WHEN CLOSING APP (NOT KILLED)
        updateQueryRoomListFromBackground();
        addNetworkListener();
        TAPBroadcastManager.register(activity, refreshTokenReceiver, REFRESH_TOKEN_RENEWED);
    }

    @Override
    public void onPause() {
        super.onPause();
        TAPNotificationManager.getInstance().setRoomListAppear(false);
        TAPBroadcastManager.unregister(activity, refreshTokenReceiver);
        removeNetworkListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        TAPBroadcastManager.unregister(activity, reloadRoomListReceiver);
        TAPBroadcastManager.unregister(activity, reloadProfilePictureReceiver);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden)
            TAPNotificationManager.getInstance().setRoomListAppear(false);
        else {
            TAPNotificationManager.getInstance().setRoomListAppear(true);
            updateQueryRoomListFromBackground();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TAPChatManager.getInstance().removeChatListener(chatListener);
        TapTalk.putGlobalChatListener();
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPRoomListViewModel.class);
    }

    private void initListener() {
        TapTalk.removeGlobalChatListener();
        chatListener = new TAPChatListener() {
            @Override
            public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onDeleteMessageInOtherRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onSendMessage(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onReadMessage(String roomID) {
                updateUnreadCountPerRoom(roomID);
            }

            @Override
            public void onReceiveStartTyping(TAPTypingModel typingModel) {
                showTypingIndicator(typingModel, true);
            }

            @Override
            public void onReceiveStopTyping(TAPTypingModel typingModel) {
                showTypingIndicator(typingModel, false);
            }
        };
        TAPChatManager.getInstance().addChatListener(chatListener);

        typingIndicatorTimeoutTimers = new HashMap<>();

        tapTalkRoomListInterface = roomModel -> {
            if (vm.getSelectedCount() > 0) {
                TapUIRoomListFragment.this.showSelectionActionBar();
            } else {
                TapUIRoomListFragment.this.hideSelectionActionBar();
            }
        };
    }

    private void initView(View view) {
        clActionBar = view.findViewById(R.id.cl_action_bar);
        clButtonSearch = view.findViewById(R.id.cl_button_search);
        //clSelection = view.findViewById(R.id.cl_selection);
        flSetupContainer = view.findViewById(R.id.fl_setup_container);
        llRoomEmpty = view.findViewById(R.id.ll_room_empty);
        llRetrySetup = view.findViewById(R.id.ll_retry_setup);
        //tvSelectionCount = view.findViewById(R.id.tv_selection_count);
        tvMyAvatarLabel = view.findViewById(R.id.tv_my_avatar_image_label);
        tvStartNewChatDescription = view.findViewById(R.id.tv_start_new_chat_description);
        tvStartNewChat = view.findViewById(R.id.tv_start_new_chat);
        tvSetupChat = view.findViewById(R.id.tv_setup_chat);
        tvSetupChatDescription = view.findViewById(R.id.tv_setup_chat_description);
        ivButtonClose = view.findViewById(R.id.iv_button_close);
        ivButtonNewChat = view.findViewById(R.id.iv_button_new_chat);
        //ivButtonCancelSelection = view.findViewById(R.id.iv_button_cancel_selection);
        //ivButtonMute = view.findViewById(R.id.iv_button_mute);
        //ivButtonDelete = view.findViewById(R.id.iv_button_delete);
        //ivButtonMore = view.findViewById(R.id.iv_button_more);
        ivSetupChat = view.findViewById(R.id.iv_setup_chat);
        ivSetupChatLoading = view.findViewById(R.id.iv_setup_chat_loading);
        rvContactList = view.findViewById(R.id.rv_contact_list);
        civMyAvatarImage = view.findViewById(R.id.civ_my_avatar_image);
        cvButtonSearch = view.findViewById(R.id.cv_button_search);
        vButtonMyAccount = view.findViewById(R.id.v_my_avatar_image);

        activity = getActivity();

        if (null != activity) {
            activity.getWindow().setBackgroundDrawable(null);
        }

        reloadProfilePicture();

        flSetupContainer.setVisibility(View.GONE);

        if (TapUI.getInstance().isSearchChatBarVisible()) {
            clActionBar.setVisibility(View.VISIBLE);
        } else {
            clActionBar.setVisibility(View.GONE);
        }

        if (TapUI.getInstance().isCloseRoomListButtonVisible()) {
            ivButtonClose.setVisibility(View.VISIBLE);
        } else {
            ivButtonClose.setVisibility(View.GONE);
        }

        if (TapUI.getInstance().isMyAccountButtonVisible()) {
            civMyAvatarImage.setVisibility(View.VISIBLE);
        } else {
            civMyAvatarImage.setVisibility(View.GONE);
        }

        if (TapUI.getInstance().isMyAccountButtonVisible()) {
            civMyAvatarImage.setVisibility(View.VISIBLE);
        } else {
            civMyAvatarImage.setVisibility(View.GONE);
        }

        if (TapUI.getInstance().isNewChatButtonVisible()) {
            tvStartNewChat.setVisibility(View.VISIBLE);
            tvStartNewChatDescription.setVisibility(View.VISIBLE);
        } else {
            tvStartNewChat.setVisibility(View.GONE);
            tvStartNewChatDescription.setVisibility(View.GONE);
        }

        if (vm.isSelecting()) {
            showSelectionActionBar();
        }
        vm.setDoneFirstApiSetup(TAPDataManager.getInstance().isRoomListSetupFinished());

        adapter = new TAPRoomListAdapter(vm, Glide.with(this), tapTalkRoomListInterface);
        llm = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(llm);
        rvContactList.setHasFixedSize(true);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        SimpleItemAnimator messageAnimator = (SimpleItemAnimator) rvContactList.getItemAnimator();
        if (null != messageAnimator) messageAnimator.setSupportsChangeAnimations(false);

        ivButtonClose.setOnClickListener(v -> TAPChatManager.getInstance().triggerCloseRoomListButtonTapped(activity));
        cvButtonSearch.setOnClickListener(v -> showSearchChat());
        vButtonMyAccount.setOnClickListener(v -> openMyAccountActivity());
        ivButtonNewChat.setOnClickListener(v -> openNewChatActivity());
        tvStartNewChat.setOnClickListener(v -> {
            TAPUtils.animateClickButton(tvStartNewChat, 0.95f);
            openNewChatActivity();
        });
        //ivButtonCancelSelection.setOnClickListener(v -> cancelSelection());
        //ivButtonMute.setOnClickListener(v -> {});
        //ivButtonDelete.setOnClickListener(v -> {});
        //ivButtonMore.setOnClickListener(v -> {});
        flSetupContainer.setOnClickListener(v -> {
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != getContext()) {
            ivButtonNewChat.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.tap_bg_start_new_chat_button_ripple));
        }
    }

    private void reloadProfilePicture() {
        // TODO: 7 May 2019 CHECK IF PROFILE IS HIDDEN
        TAPUserModel user = TAPChatManager.getInstance().getActiveUser();
        if (null != user && null != user.getAvatarURL()
                && !user.getAvatarURL().getThumbnail().isEmpty()) {
            if (null != getActivity()) {
                getActivity().runOnUiThread(() -> {
                    Glide.with(this).load(user.getAvatarURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Show initial
                            if (null != getActivity()) {
                                getActivity().runOnUiThread(() -> {
                                    ImageViewCompat.setImageTintList(civMyAvatarImage, ColorStateList.valueOf(TAPUtils.getRandomColor(getContext(), user.getName())));
                                    if (null != getContext()) {
                                        civMyAvatarImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.tap_bg_circle_9b9b9b));
                                    }
                                });
                            }
                            tvMyAvatarLabel.setText(TAPUtils.getInitials(user.getName(), 2));
                            tvMyAvatarLabel.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(civMyAvatarImage);
                    ImageViewCompat.setImageTintList(civMyAvatarImage, null);
                    tvMyAvatarLabel.setVisibility(View.GONE);
                });
            }
        } else if (null != user) {
            // Show initial
            if (null != getActivity()) {
                getActivity().runOnUiThread(() -> {
                    ImageViewCompat.setImageTintList(civMyAvatarImage, ColorStateList.valueOf(TAPUtils.getRandomColor(getContext(), user.getName())));
                    if (null != getContext()) {
                        civMyAvatarImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.tap_bg_circle_9b9b9b));
                    }
                });
            }
            tvMyAvatarLabel.setText(TAPUtils.getInitials(user.getName(), 2));
            tvMyAvatarLabel.setVisibility(View.VISIBLE);
        }
    }

    private void showSearchChat() {
        TAPUtils.animateClickButton(cvButtonSearch, 0.97f);
        TAPChatManager.getInstance().triggerSearchChatBarTapped(activity, mainRoomListFragment);
    }

    private void openMyAccountActivity() {
        TAPChatManager.getInstance().triggerTapTalkAccountButtonTapped(activity);
    }

    private void openNewChatActivity() {
        TAPChatManager.getInstance().triggerNewChatButtonTapped(activity);
    }

    private void viewLoadedSequence() {
        if (TAPRoomListViewModel.isShouldNotLoadFromAPI() && null != TAPChatManager.getInstance().getActiveUser()) {
            // Load room list from database if app is on foreground
            TAPDataManager.getInstance().getRoomList(true, dbListener);
        } else if (null != TAPChatManager.getInstance().getActiveUser()) {
            // Run full cycle if app is on background or on first open
            // TODO: 18 Feb 2020 DATABASE FIRST QUERY CALLED TWICE WHEN CLOSING APP (NOT KILLED)
            Log.e(TAG, "viewLoadedSequence: runFullRefreshSequence");
            runFullRefreshSequence();
        } else if (TapTalk.checkTapTalkInitialized() && TapTalk.isAuthenticated()) {
            AnalyticsManager.getInstance().trackEvent("View Loaded Sequence Failed");
            TapTalk.clearAllTapTalkData();
            for (TapListener listener : TapTalk.getTapTalkListeners()) {
                listener.onTapTalkRefreshTokenExpired();
            }
        } else if (null == TAPChatManager.getInstance().getActiveUser()) {
            flSetupContainer.setVisibility(View.VISIBLE);
            showChatRoomSetupFailed();
            if (BuildConfig.DEBUG && null != activity) {
                new TapTalkDialog.Builder(activity)
                        .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                        .setTitle(getString(R.string.tap_error))
                        .setMessage(getString(R.string.tap_error_active_user_is_null))
                        .setCancelable(false)
                        .setPrimaryButtonTitle(getString(R.string.tap_ok))
                        .show();
            }
            Log.e(TAG, getString(R.string.tap_error_active_user_is_null));
        }
    }

    private void getDatabaseAndAnimateResult() {
        TAPDataManager.getInstance().getRoomList(true, dbAnimatedListener);
    }

    private void runFullRefreshSequence() {
        if (vm.getRoomList().size() > 0) {
            // Check and update unread badge before updating view if recycler is not empty
            TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), true, dbListener);
        } else {
            // Update unread badge after view is updated if recycler is empty
            TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), false, dbListener);
        }
        TAPGroupManager.Companion.getGetInstance().setRefreshRoomList(false);
    }

    private void fetchDataFromAPI() {
        if (vm.isDoneFirstApiSetup()) {
            // Call to refresh new messages
            TAPDataManager.getInstance().getNewAndUpdatedMessage(roomListView);
        } else {
            // Call on first load
            TAPDataManager.getInstance().getMessageRoomListAndUnread(TAPChatManager.getInstance().getActiveUser().getUserID(), roomListView);
        }
    }

    private void reloadLocalDataAndUpdateUILogic(boolean isAnimated) {
        if (null != activity) {
            activity.runOnUiThread(() -> {
                if (null != adapter && 0 == vm.getRoomList().size()) {
                    // Room list is empty
                    llRoomEmpty.setVisibility(View.VISIBLE);
                } else if (null != adapter && (!TAPRoomListViewModel.isShouldNotLoadFromAPI() || isAnimated) && TAPNotificationManager.getInstance().isRoomListAppear()) {
                    // Show room list on first open and animate
                    adapter.addRoomList(vm.getRoomList());
                    rvContactList.scrollToPosition(0);
                    llRoomEmpty.setVisibility(View.GONE);
                } else if (null != adapter && TAPRoomListViewModel.isShouldNotLoadFromAPI()) {
                    // Update room list without animating
                    adapter.setItems(vm.getRoomList(), false);
                    llRoomEmpty.setVisibility(View.GONE);
                }
                flSetupContainer.setVisibility(View.GONE);
                showNewChatButton();

                if (!TAPRoomListViewModel.isShouldNotLoadFromAPI()) {
                    TAPRoomListViewModel.setShouldNotLoadFromAPI(true);
                    fetchDataFromAPI();
                }
            });
        }
    }

    private void processMessageFromSocket(TAPMessageModel message) {
        String messageRoomID = message.getRoom().getRoomID();
        TAPRoomListModel roomList = vm.getRoomPointer().get(messageRoomID);

        if (null != roomList && null != message.getHidden() && !message.getHidden()) {
            // Received message in an existing room list
            roomList.setLastMessageTimestamp(message.getCreated());
            TAPMessageModel roomLastMessage = roomList.getLastMessage();

            if (roomLastMessage.getLocalID().equals(message.getLocalID()) && null != activity) {
                // Update room list's last message data
                roomLastMessage.updateValue(message);
                activity.runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(roomList)));
            } else if (roomLastMessage.getCreated() < message.getCreated()) {
                int oldPos = vm.getRoomList().indexOf(roomList);
                if (LEAVE_ROOM.equals(message.getAction()) && vm.getMyUserID().equals(message.getUser().getUserID())) {
                    // Remove room from list
                    vm.getRoomList().remove(roomList);
                    vm.getRoomPointer().remove(roomList.getLastMessage().getLocalID());
                    activity.runOnUiThread(() -> adapter.notifyItemRemoved(oldPos));
                } else {
                    // Update room list's last message with the new message from socket
                    roomList.setLastMessage(message);

                    // Add unread count by 1 if sender is not self
                    if (!roomList.getLastMessage().getUser().getUserID()
                            .equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
                        roomList.setUnreadCount(roomList.getUnreadCount() + 1);
                    }

                    // Move room to top
                    vm.getRoomList().remove(roomList);
                    vm.getRoomList().add(0, roomList);

                    if (null != activity) {
                        activity.runOnUiThread(() -> {
                            llRoomEmpty.setVisibility(View.GONE);
                            adapter.notifyItemChanged(oldPos);
                            adapter.notifyItemMoved(oldPos, 0);
                            // Scroll to top
                            if (llm.findFirstCompletelyVisibleItemPosition() == 0)
                                rvContactList.scrollToPosition(0);
                        });
                    }
                }
            }
        } else if (null != activity && null != message.getHidden() && !message.getHidden()) {
            // Received message in a new room list
            TAPRoomListModel newRoomList = TAPRoomListModel.buildWithLastMessage(message);
            if (!newRoomList.getLastMessage().getUser().getUserID()
                    .equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
                newRoomList.setUnreadCount(1);
            }

            vm.addRoomPointer(newRoomList);
            vm.getRoomList().add(0, newRoomList);
            activity.runOnUiThread(() -> {
                llRoomEmpty.setVisibility(View.GONE);
                if (0 == adapter.getItems().size())
                    adapter.addItem(newRoomList);
                adapter.notifyItemInserted(0);

                if (llm.findFirstCompletelyVisibleItemPosition() == 0)
                    rvContactList.scrollToPosition(0);
            });
        }

        if (null != roomList && message.getType() == TYPE_SYSTEM_MESSAGE &&
                null != message.getAction() &&
                (message.getAction().equals(UPDATE_ROOM) || message.getAction().equals(UPDATE_USER))) {
            // Update room details
            activity.runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(roomList)));
        }
        updateProfilePictureFromSystemMessage(message);
        calculateBadgeCount();
    }

    private void updateProfilePictureFromSystemMessage(TAPMessageModel message) {
        if (message.getType() == TYPE_SYSTEM_MESSAGE &&
                null != message.getAction() &&
                message.getAction().equals(UPDATE_USER) &&
                message.getRoom().getRoomID().equals(TAPChatManager.getInstance().arrangeRoomId(
                        TAPChatManager.getInstance().getActiveUser().getUserID(),
                        TAPChatManager.getInstance().getActiveUser().getUserID())) &&
                civMyAvatarImage.getVisibility() == View.VISIBLE) {
            // Update user avatar
            reloadProfilePicture();
        }
    }

    private void showSelectionActionBar() {
        vm.setSelecting(true);
        //tvSelectionCount.setText(vm.getSelectedCount() + "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clButtonSearch.setElevation(0);
        }
        clButtonSearch.setVisibility(View.INVISIBLE);
        //clSelection.setVisibility(View.VISIBLE);
    }

    private void hideSelectionActionBar() {
        vm.setSelecting(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clButtonSearch.setElevation(TAPUtils.dpToPx(2));
        }
        clButtonSearch.setVisibility(View.VISIBLE);
        //clSelection.setVisibility(View.INVISIBLE);
    }

    public void cancelSelection() {
        vm.getSelectedRooms().clear();
        adapter.notifyDataSetChanged();
        hideSelectionActionBar();
    }

    private void showNewChatButton() {
        if (!TapUI.getInstance().isNewChatButtonVisible() || ivButtonNewChat.getVisibility() == View.VISIBLE) {
            return;
        }
        ivButtonNewChat.setTranslationY(TAPUtils.dpToPx(120));
        ivButtonNewChat.setVisibility(View.VISIBLE);
        ivButtonNewChat.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void hideNewChatButton() {
        if (ivButtonNewChat.getVisibility() == View.GONE || ivButtonNewChat.getTranslationY() > 0) {
            return;
        }
        ivButtonNewChat.animate()
                .translationY(TAPUtils.dpToPx(120))
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> ivButtonNewChat.setVisibility(View.GONE))
                .start();
    }

    public boolean isSelecting() {
        return vm.isSelecting();
    }

    private void showTypingIndicator(TAPTypingModel typingModel, boolean isTyping) {
        String roomID = typingModel.getRoomID();
        if (null == activity || !vm.getRoomPointer().containsKey(roomID) || null == typingModel.getUser()) {
            return;
        }
        TAPRoomListModel roomListModel = vm.getRoomPointer().get(roomID);
        if (null == roomListModel) {
            return;
        }

        activity.runOnUiThread(() -> {
            if (isTyping) {
                roomListModel.addTypingUsers(typingModel.getUser());
                CountDownTimer roomTimer = typingIndicatorTimeoutTimers.get(roomID);
                if (null != roomTimer) {
                    roomTimer.cancel();
                } else {
                    roomTimer = new CountDownTimer(TYPING_INDICATOR_TIMEOUT, 1000L) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            // Hide typing status on countdown finish
                            roomListModel.getTypingUsers().clear();
                            typingIndicatorTimeoutTimers.remove(roomID);
                            adapter.notifyItemChanged(vm.getRoomList().indexOf(roomListModel));
                        }
                    };
                    typingIndicatorTimeoutTimers.put(roomID, roomTimer);
                }
                // Restart typing timeout timer
                roomTimer.start();
            } else {
                roomListModel.removeTypingUser(typingModel.getUser().getUserID());
            }

            adapter.notifyItemChanged(vm.getRoomList().indexOf(roomListModel));
        });
    }

    private void showChatRoomSettingUp() {
        activity.runOnUiThread(() -> {
            ivSetupChat.setImageDrawable(getResources().getDrawable(R.drawable.tap_ic_setting_up_grey));
            ivSetupChatLoading.setImageDrawable(getResources().getDrawable(R.drawable.tap_ic_loading_progress_circle_white));
            ivSetupChat.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconRoomListSettingUp));
            ivSetupChatLoading.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconLoadingProgressPrimary));
            tvSetupChat.setText(getString(R.string.tap_chat_room_setting_up));
            tvSetupChatDescription.setText(getString(R.string.tap_chat_room_setting_up_description));
            TAPUtils.rotateAnimateInfinitely(activity, ivSetupChatLoading);

            tvSetupChatDescription.setVisibility(View.VISIBLE);
            llRetrySetup.setVisibility(View.GONE);
            flSetupContainer.setVisibility(View.VISIBLE);

            llRetrySetup.setOnClickListener(null);

            hideNewChatButton();
        });
    }

    private void showChatRoomSetupSuccess() {
        activity.runOnUiThread(() -> {
            ivSetupChat.setImageDrawable(getResources().getDrawable(R.drawable.tap_ic_setup_success_green));
            ivSetupChatLoading.setImageDrawable(getResources().getDrawable(R.drawable.tap_ic_loading_progress_full_circle_red));
            ivSetupChat.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconRoomListSetupSuccess));
            ivSetupChatLoading.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconRoomListSetupSuccess));
            ivSetupChatLoading.clearAnimation();
            tvSetupChat.setText(getString(R.string.tap_chat_room_setup_success));
            tvSetupChatDescription.setText(getString(R.string.tap_chat_room_setup_success_description));

            tvSetupChatDescription.setVisibility(View.VISIBLE);
            llRetrySetup.setVisibility(View.GONE);

            llRetrySetup.setOnClickListener(null);

            showNewChatButton();

            new Handler().postDelayed(() -> flSetupContainer.setVisibility(View.GONE), 2000L);
        });
    }

    private void showChatRoomSetupFailed() {
        activity.runOnUiThread(() -> {
            ivSetupChat.setImageDrawable(getResources().getDrawable(R.drawable.tap_ic_setup_failed_red));
            ivSetupChatLoading.setImageDrawable(getResources().getDrawable(R.drawable.tap_ic_loading_progress_full_circle_red));
            ivSetupChat.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconRoomListSetupFailure));
            ivSetupChatLoading.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconRoomListSetupFailure));
            ivSetupChatLoading.clearAnimation();
            tvSetupChat.setText(getString(R.string.tap_chat_room_setup_failed));

            tvSetupChatDescription.setVisibility(View.GONE);
            llRetrySetup.setVisibility(View.VISIBLE);

            llRetrySetup.setOnClickListener(view -> {
                TAPUtils.animateClickButton(llRetrySetup, 0.95f);
                if (null == TAPChatManager.getInstance().getActiveUser()) {
                    return;
                }
                TAPDataManager.getInstance().getMessageRoomListAndUnread(
                        TAPChatManager.getInstance().getActiveUser().getUserID(), roomListView);
            });
        });
    }

    private TAPDefaultDataView<TAPGetRoomListResponse> roomListView = new TAPDefaultDataView<TAPGetRoomListResponse>() {
        @Override
        public void startLoading() {
            if (!vm.isDoneFirstApiSetup()) {
                // Show setup dialog when app is opened for the first time
                showChatRoomSettingUp();
            }
        }

        @Override
        public void onSuccess(TAPGetRoomListResponse response) {
            vm.setDoneFirstSetup(true);

            if (response.getMessages().size() > 0) {
                List<TAPMessageEntity> tempMessage = new ArrayList<>();
                List<TAPMessageModel> deliveredMessages = new ArrayList<>();
                List<String> userIds = new ArrayList<>();
                TAPMessageModel updateProfileSystemMessage = null;
                for (HashMap<String, Object> messageMap : response.getMessages()) {
                    try {
                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                        TAPMessageEntity entity = TAPChatManager.getInstance().convertToEntity(message);
                        tempMessage.add(entity);

                        // Save undelivered messages to list
                        if (null == message.getDelivered() || (null != message.getDelivered() && !message.getDelivered())) {
                            deliveredMessages.add(message);
                        }

                        if (message.getUser().getUserID().equals(TAPChatManager.getInstance().getActiveUser().getUserID())) {
                            // User is self, get other user data from API
                            userIds.add(TAPChatManager.getInstance().getOtherUserIdFromRoom(message.getRoom().getRoomID()));
                        } else {
                            // Save user data to contact manager
                            TAPContactManager.getInstance().updateUserData(message.getUser());
                        }
                        if (null != message.getIsDeleted() && message.getIsDeleted()) {
                            TAPDataManager.getInstance().deletePhysicalFile(entity);
                        }

                        if (message.getType() == TYPE_SYSTEM_MESSAGE &&
                                null != message.getAction() &&
                                message.getAction().equals(UPDATE_USER) &&
                                (null == updateProfileSystemMessage ||
                                        updateProfileSystemMessage.getCreated() < message.getCreated())) {
                            // Store update profile system message
                            updateProfileSystemMessage = message;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (null != updateProfileSystemMessage) {
                    // Update room detail if update room system message exists in API result
                    updateProfilePictureFromSystemMessage(updateProfileSystemMessage);
                }

                // Update status to delivered
                if (deliveredMessages.size() > 0) {
                    TAPMessageStatusManager.getInstance().updateMessageStatusToDelivered(deliveredMessages);
                }

                // Get updated other user data from API
                if (userIds.size() > 0) {
                    TAPDataManager.getInstance().getMultipleUsersByIdFromApi(userIds, getMultipleUserView);
                }

                // Save message to database
                TAPDataManager.getInstance().insertToDatabase(tempMessage, false, new TAPDatabaseListener() {
                    @Override
                    public void onInsertFinished() {
                        // Reload newest room list from database
                        getDatabaseAndAnimateResult();
                    }
                });
            } else {
                reloadLocalDataAndUpdateUILogic(true);
                if (!vm.isDoneFirstApiSetup()) {
                    vm.setDoneFirstApiSetup(true);
                    TAPDataManager.getInstance().setRoomListSetupFinished();
                    showChatRoomSetupSuccess();
                }
            }

            calculateBadgeCount();
        }

        @Override
        public void onError(TAPErrorModel error) {
            onError(error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            if (!vm.isDoneFirstApiSetup()) {
                showChatRoomSetupFailed();
            } else {
                flSetupContainer.setVisibility(View.GONE);
                showNewChatButton();
            }
        }
    };

    private TAPDefaultDataView<TAPGetMultipleUserResponse> getMultipleUserView = new TAPDefaultDataView<TAPGetMultipleUserResponse>() {
        @Override
        public void onSuccess(TAPGetMultipleUserResponse response) {
            if (null == response || response.getUsers().isEmpty()) {
                return;
            }
            new Thread(() -> TAPContactManager.getInstance().updateUserData(response.getUsers())).start();
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> dbListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            List<TAPRoomListModel> messageModels = new ArrayList<>();
            vm.getRoomPointer().clear();
            int count = 0; // FIXME Count to load room list every 10 items
            int limit = 10;
            // Convert entity to model
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPChatManager.getInstance().convertToModel(entity);
                TAPRoomListModel roomModel = TAPRoomListModel.buildWithLastMessage(model);
                messageModels.add(roomModel);
                vm.addRoomPointer(roomModel);
                TAPDataManager.getInstance().getUnreadCountPerRoom(entity.getRoomID(), dbListener);
                if (++count % limit == 0) {
                    vm.setRoomList(messageModels);
                    activity.runOnUiThread(() -> adapter.setItems(vm.getRoomList()));
                    limit = limit * 2;
                }
            }

            vm.setRoomList(messageModels);
            reloadLocalDataAndUpdateUILogic(false);
            calculateBadgeCount();
        }

        @Override
        public void onCountedUnreadCount(String roomID, int unreadCount) {
            try {
                if (null != activity && vm.getRoomPointer().containsKey(roomID) && null != vm.getRoomPointer().get(roomID)) {
                    vm.getRoomPointer().get(roomID).setUnreadCount(unreadCount);
                    activity.runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID))));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap) {
            List<TAPRoomListModel> messageModels = new ArrayList<>();
            vm.getRoomPointer().clear();
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPChatManager.getInstance().convertToModel(entity);
                TAPRoomListModel roomModel = TAPRoomListModel.buildWithLastMessage(model);
                messageModels.add(roomModel);
                vm.addRoomPointer(roomModel);
                if (null != vm.getRoomPointer().get(entity.getRoomID()) && null != unreadMap.get(entity.getRoomID())) {
                    vm.getRoomPointer().get(entity.getRoomID()).setUnreadCount(unreadMap.get(entity.getRoomID()));
                }
            }

            vm.setRoomList(messageModels);
            reloadLocalDataAndUpdateUILogic(false);
            calculateBadgeCount();
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> dbAnimatedListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap) {
            List<TAPRoomListModel> messageModels = new ArrayList<>();
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPChatManager.getInstance().convertToModel(entity);
                TAPRoomListModel roomModel = TAPRoomListModel.buildWithLastMessage(model);
                messageModels.add(roomModel);
                vm.addRoomPointer(roomModel);
                if (null != vm.getRoomPointer().get(entity.getRoomID()) && null != unreadMap.get(entity.getRoomID())) {
                    vm.getRoomPointer().get(entity.getRoomID()).setUnreadCount(unreadMap.get(entity.getRoomID()));
                }
            }
            vm.setRoomList(messageModels);
            reloadLocalDataAndUpdateUILogic(true);
            calculateBadgeCount();
            if (!vm.isDoneFirstApiSetup()) {
                vm.setDoneFirstApiSetup(true);
                TAPDataManager.getInstance().setRoomListSetupFinished();
                showChatRoomSetupSuccess();
            }
        }
    };

    private void updateQueryRoomListFromBackground() {
        if (TAPDataManager.getInstance().isNeedToQueryUpdateRoomList()) {
            runFullRefreshSequence();
            TAPDataManager.getInstance().setNeedToQueryUpdateRoomList(false);
        }
    }

    private void addNetworkListener() {
        TAPNetworkStateManager.getInstance().addNetworkListener(networkListener);
    }

    private void removeNetworkListener() {
        TAPNetworkStateManager.getInstance().removeNetworkListener(networkListener);
    }

    private void updateUnreadCountPerRoom(String roomID) {
        new Thread(() -> {
            if (null != getActivity() && vm.getRoomPointer().containsKey(roomID) &&
                    TAPMessageStatusManager.getInstance().getUnreadList().containsKey(roomID) &&
                    TAPMessageStatusManager.getInstance().getUnreadList().get(roomID) <= vm.getRoomPointer().get(roomID).getUnreadCount()) {
                vm.getRoomPointer().get(roomID).setUnreadCount(vm.getRoomPointer().get(roomID).getUnreadCount() - TAPMessageStatusManager.getInstance().getUnreadList().get(roomID));
                TAPMessageStatusManager.getInstance().clearUnreadListPerRoomID(roomID);
                getActivity().runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID))));
            } else if (null != getActivity() && vm.getRoomPointer().containsKey(roomID) &&
                    TAPMessageStatusManager.getInstance().getUnreadList().containsKey(roomID)) {
                vm.getRoomPointer().get(roomID).setUnreadCount(0);
                TAPMessageStatusManager.getInstance().clearUnreadListPerRoomID(roomID);
                getActivity().runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID))));
            }
            calculateBadgeCount();
        }).start();
    }

    private BroadcastReceiver refreshTokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent.getAction() || !intent.getAction().equals(REFRESH_TOKEN_RENEWED)) {
                return;
            }
            viewLoadedSequence();
        }
    };

    private BroadcastReceiver reloadRoomListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent.getAction() || null == adapter) {
                return;
            }
            String roomID = intent.getStringExtra(ROOM_ID);
            switch (intent.getAction()) {
                case RELOAD_ROOM_LIST:
                    adapter.notifyItemChanged(vm.getRoomList().indexOf(
                            vm.getRoomPointer().get(roomID)));
                    break;
                case CLEAR_ROOM_LIST_BADGE:
                    if (vm.getRoomPointer().containsKey(roomID)) {
                        vm.getRoomPointer().get(roomID).setUnreadCount(0);
                        TAPMessageStatusManager.getInstance().clearUnreadListPerRoomID(roomID);
                    }
                    break;
            }
        }
    };

    private BroadcastReceiver reloadProfilePictureReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent.getAction() && RELOAD_PROFILE_PICTURE.equals(intent.getAction())) {
                reloadProfilePicture();
            }
        }
    };

    private void calculateBadgeCount() {
        vm.setRoomBadgeCount(0);
        try {
            for (Map.Entry<String, TAPRoomListModel> entry : vm.getRoomPointer().entrySet()) {
                vm.setRoomBadgeCount(vm.getRoomBadgeCount() + entry.getValue().getUnreadCount());
            }
        } catch (ConcurrentModificationException e) { // FIXME: 5 Dec 2019
            e.printStackTrace();
        }
        if (vm.getLastBadgeCount() != vm.getRoomBadgeCount()) {
            for (TapListener listener : TapTalk.getTapTalkListeners()) {
                listener.onTapTalkUnreadChatRoomBadgeCountUpdated(vm.getRoomBadgeCount());
            }
            vm.setLastBadgeCount(vm.getRoomBadgeCount());
        }
    }
}
