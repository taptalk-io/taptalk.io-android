package io.taptalk.TapTalk.View.Fragment;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST_BADGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.IS_NEED_REFRESH;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OPEN_CHAT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.REFRESH_TOKEN_RENEWED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_PROFILE_PICTURE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.DELETE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.LEAVE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;
import static io.taptalk.TapTalk.Helper.TapTalk.getTapTalkListeners;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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
import io.taptalk.TapTalk.BuildConfig;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.SwipedListItem.OnMoveAndSwipeListener;
import io.taptalk.TapTalk.Helper.SwipedListItem.TapRoomListItemSwipeCallback;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPGeneralListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TapCoreChatRoomManager;
import io.taptalk.TapTalk.Manager.TapCoreContactManager;
import io.taptalk.TapTalk.Manager.TapCoreMessageManager;
import io.taptalk.TapTalk.Manager.TapCoreRoomListManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMultipleUserResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetRoomIdsWithStateResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapGetUnreadRoomIdsResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TapMutedRoomListModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.View.Adapter.TAPRoomListAdapter;
import io.taptalk.TapTalk.View.BottomSheet.TapMuteBottomSheet;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;

public class TapUIRoomListFragment extends Fragment {

    private final String TAG = TapUIRoomListFragment.class.getSimpleName();

    private String instanceKey = "";
    private Activity activity;
    private TapUIMainRoomListFragment mainRoomListFragment;
    private TAPSocketListener socketListener;

    private ConstraintLayout clActionBar, clButtonSearch, clSelection;
    private FrameLayout flSetupContainer;
    private LinearLayout llRoomEmpty, llRetrySetup;
    private TextView tvSelectionCount, tvMyAvatarLabel, tvStartNewChatDescription, tvStartNewChat, tvSetupChat, tvSetupChatDescription;
    private ImageView ivButtonClose, ivButtonNewChat, ivButtonCancelSelection, ivButtonMute, ivButtonDelete, ivButtonMore, ivSetupChat, ivSetupChatLoading;
    private CircleImageView civMyAvatarImage;
    private CardView cvButtonSearch;
    private View vButtonMyAccount;

    private TAPChatRecyclerView rvContactList;
    private LinearLayoutManager llm;
    private TAPRoomListAdapter adapter;
    private TapTalkRoomListInterface tapTalkRoomListInterface;
    private TAPRoomListViewModel vm;
    private HashMap<String, CountDownTimer> typingIndicatorTimeoutTimers;
    private RequestManager glide;
    private TapTalkDialog userNullErrorDialog;

    private TAPChatListener chatListener;

    private final TapTalkNetworkInterface networkListener = () -> {
        if (vm.isDoneFirstSetup()) {
            updateQueryRoomListFromBackground();
        }
    };

    public TapUIRoomListFragment() {
    }

    public TapUIRoomListFragment(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TapUIRoomListFragment newInstance(String instanceKey) {
        return new TapUIRoomListFragment(instanceKey);
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
        bindViews(view);
        initView();
        viewLoadedSequence();
        TapUI.getInstance(instanceKey).setCurrentTapTalkRoomListFragment(this);
        TAPBroadcastManager.register(
            activity,
            roomListBroadcastReceiver,
            REFRESH_TOKEN_RENEWED,
            RELOAD_PROFILE_PICTURE,
            RELOAD_ROOM_LIST,
            CLEAR_ROOM_LIST_BADGE,
            CLEAR_ROOM_LIST,
            OPEN_CHAT
        );
        checkAndUpdateContactList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TAPGroupManager.Companion.getInstance(instanceKey).getRefreshRoomList()) {
            runFullRefreshSequence();
        }
        // TODO: 29 October 2018 UPDATE UNREAD BADGE
        TAPNotificationManager.getInstance(instanceKey).setRoomListAppear(true);
        new Thread(() -> TAPChatManager.getInstance(instanceKey).saveMessageToDatabase()).start();
        // TODO: 18 Feb 2020 DATABASE FIRST QUERY CALLED TWICE WHEN CLOSING APP (NOT KILLED)
        updateQueryRoomListFromBackground();
        addNetworkListener();
        openTapTalkSocketWhenNeeded();
    }

    @Override
    public void onPause() {
        super.onPause();
        TAPNotificationManager.getInstance(instanceKey).setRoomListAppear(false);
        removeNetworkListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeSocketListener();
        TAPBroadcastManager.unregister(activity, roomListBroadcastReceiver);
        closeTapTalkSocketWhenNeeded();
        TAPDataManager.getInstance(instanceKey).unsubscribeRoomListAndUnreadApi();
        TapUI.getInstance(instanceKey).setCurrentTapTalkRoomListFragment(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TAPChatManager.getInstance(instanceKey).removeChatListener(chatListener);
        TapTalk.getTapTalkInstance(instanceKey).putGlobalChatListener();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden)
            TAPNotificationManager.getInstance(instanceKey).setRoomListAppear(false);
        else {
            TAPNotificationManager.getInstance(instanceKey).setRoomListAppear(true);
            updateQueryRoomListFromBackground();
        }
    }

    private void initViewModel() {
        activity = getActivity();
        if (null == activity || null == getContext()) {
            return;
        }

        vm = new ViewModelProvider(this,
                new TAPRoomListViewModel.TAPRoomListViewModelFactory(
                        activity.getApplication(), instanceKey))
                .get(TAPRoomListViewModel.class);
    }

    private void initListener() {
        TapTalk.getTapTalkInstance(instanceKey).removeGlobalChatListener();
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
                TAPRoomListModel roomListModel = vm.getRoomPointer().get(roomID);
                if (roomListModel != null && roomListModel.isMarkedAsUnread()) {
                    TapCoreMessageManager.getInstance(instanceKey).markMessageAsRead(roomListModel.getLastMessage().getMessageID());
                }
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

            @Override
            public void onChatCleared(TAPRoomModel room) {
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                activity.runOnUiThread(() -> {
                    updatePinnedRooms(room.getRoomID(), false);
                    updateRoomUnreadMark(room.getRoomID(), false);
                    updateMutedRooms(room.getRoomID(), false);
                    vm.getRoomPointer().remove(room.getRoomID());
                    int index = vm.getRoomList().indexOf(vm.getRoomPointer().get(room.getRoomID()));
                    if (index >= 0 && index < vm.getRoomList().size()) {
                        vm.getRoomList().remove(index);
                        adapter.notifyItemRemoved(index);
                    }
                });
            }

            @Override
            public void onMuteOrUnmuteRoom(TAPRoomModel room, Long expiredAt) {
                super.onMuteOrUnmuteRoom(room, expiredAt);
                String roomId = room.getRoomID();
                boolean isMuted = TAPDataManager.getInstance(instanceKey).getMutedRoomIDs().containsKey(roomId);
                updateMutedRooms(roomId, isMuted);
                if (null != getActivity()) {
                    getActivity().runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomId))));
                }
            }

            @Override
            public void onPinRoom(TAPRoomModel room) {
                super.onPinRoom(room);
                ArrayList<String> pinnedRooms = TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs();
                boolean isPinned = pinnedRooms.contains(room.getRoomID());
                updatePinnedRooms(room.getRoomID(), isPinned);
                int targetIndex = 0;
                int position = vm.getRoomList().indexOf(vm.getRoomPointer().get(room.getRoomID()));
                vm.getRoomList().add(targetIndex, vm.getRoomList().remove(position));
                if (null != getActivity()) {
                    getActivity().runOnUiThread(() -> {
                        adapter.notifyItemMoved(position, targetIndex);
                        adapter.notifyItemChanged(targetIndex);
                        rvContactList.scrollToPosition(0);
                    });
                }
            }

            @Override
            public void onUnpinRoom(TAPRoomModel room) {
                super.onUnpinRoom(room);
                ArrayList<String> pinnedRooms = TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs();
                boolean isPinned = pinnedRooms.contains(room.getRoomID());
                updatePinnedRooms(room.getRoomID(), isPinned);
                int targetIndex = pinnedRooms.size();
                int position = vm.getRoomList().indexOf(vm.getRoomPointer().get(room.getRoomID()));
                if (pinnedRooms.size() + 1 < vm.getRoomList().size()) {
                    for (int i = pinnedRooms.size() + 1; i < vm.getRoomList().size(); i++) {
                        if (vm.getRoomList().get(position).getLastMessage().getCreated() > vm.getRoomList().get(i).getLastMessage().getCreated()) {
                            targetIndex = i - 1;
                            break;
                        }
                    }
                }
                else {
                    targetIndex = vm.getRoomList().size() - 1;
                }
                vm.getRoomList().add(targetIndex, vm.getRoomList().remove(position));
                if (null != getActivity()) {
                    int finalTargetIndex = targetIndex;
                    getActivity().runOnUiThread(() -> {
                        adapter.notifyItemMoved(position, finalTargetIndex);
                        adapter.notifyItemChanged(finalTargetIndex);
                        rvContactList.scrollToPosition(0);
                    });
                }
            }

            @Override
            public void onMarkRoomAsRead(String roomID) {
                super.onMarkRoomAsRead(roomID);
                boolean isMarkedAsUnread = TAPDataManager.getInstance(instanceKey).getUnreadRoomIDs().contains(roomID);
                updateRoomUnreadMark(roomID, isMarkedAsUnread);
                if (null != getActivity()) {
                    getActivity().runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID))));
                }
            }

            @Override
            public void onMarkRoomAsUnread(String roomID) {
                super.onMarkRoomAsUnread(roomID);
                boolean isMarkedAsUnread = TAPDataManager.getInstance(instanceKey).getUnreadRoomIDs().contains(roomID);
                updateRoomUnreadMark(roomID, isMarkedAsUnread);
                if (null != getActivity()) {
                    getActivity().runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID))));
                }
            }
        };
        TAPChatManager.getInstance(instanceKey).addChatListener(chatListener);

        typingIndicatorTimeoutTimers = new HashMap<>();

        tapTalkRoomListInterface = roomModel -> {
            if (vm.getSelectedCount() > 0) {
                TapUIRoomListFragment.this.showSelectionActionBar();
            }
            else {
                TapUIRoomListFragment.this.hideSelectionActionBar();
            }
        };
        addSocketListener();
    }

    private void bindViews(View view) {
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
    }

    private void initView() {
        if (null != activity) {
            activity.getWindow().setBackgroundDrawable(null);
        }

        glide = Glide.with(this);

        reloadProfilePicture();

        flSetupContainer.setVisibility(View.GONE);

        if (TapUI.getInstance(instanceKey).isSearchChatBarVisible()) {
            clActionBar.setVisibility(View.VISIBLE);
        }
        else {
            clActionBar.setVisibility(View.GONE);
        }

        if (TapUI.getInstance(instanceKey).isCloseRoomListButtonVisible()) {
            ivButtonClose.setVisibility(View.VISIBLE);
        }
        else {
            ivButtonClose.setVisibility(View.GONE);
        }

        if (TapUI.getInstance(instanceKey).isMyAccountButtonVisible()) {
            civMyAvatarImage.setVisibility(View.VISIBLE);
        }
        else {
            civMyAvatarImage.setVisibility(View.GONE);
        }

        if (TapUI.getInstance(instanceKey).isMyAccountButtonVisible()) {
            civMyAvatarImage.setVisibility(View.VISIBLE);
        }
        else {
            civMyAvatarImage.setVisibility(View.GONE);
        }

        if (TapUI.getInstance(instanceKey).isNewChatButtonVisible()) {
            tvStartNewChat.setVisibility(View.VISIBLE);
            tvStartNewChatDescription.setVisibility(View.VISIBLE);
        }
        else {
            tvStartNewChat.setVisibility(View.GONE);
            tvStartNewChatDescription.setVisibility(View.GONE);
        }

        if (vm.isSelecting()) {
            showSelectionActionBar();
        }
        vm.setDoneFirstApiSetup(TAPDataManager.getInstance(instanceKey).isRoomListSetupFinished());

        adapter = new TAPRoomListAdapter(instanceKey, vm, glide, tapTalkRoomListInterface);
        llm = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                }
                catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        rvContactList.instanceKey = instanceKey;
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(llm);
        rvContactList.setHasFixedSize(true);
        float clamp;
        try {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            clamp = Math.round(64 * (float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        }
        catch (Exception e) {
            clamp = 48f;
        }
        TapRoomListItemSwipeCallback tapRoomListItemSwipeCallback = new TapRoomListItemSwipeCallback(instanceKey);
        tapRoomListItemSwipeCallback.setClamp(clamp, -clamp);
        tapRoomListItemSwipeCallback.setListener(onMoveAndSwipeListener);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(tapRoomListItemSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rvContactList);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        SimpleItemAnimator messageAnimator = (SimpleItemAnimator) rvContactList.getItemAnimator();
        if (null != messageAnimator) messageAnimator.setSupportsChangeAnimations(false);

        ivButtonClose.setOnClickListener(v -> TAPChatManager.getInstance(instanceKey).triggerCloseRoomListButtonTapped(activity));
        cvButtonSearch.setOnClickListener(v -> showSearchChat());
        vButtonMyAccount.setOnClickListener(v -> openMyAccountActivity());
        ivButtonNewChat.setOnClickListener(v -> openNewChatActivity());
        tvStartNewChat.setOnClickListener(v -> {
            TAPUtils.animateClickButton(tvStartNewChat, 0.95f);
            openNewChatActivity();
        });
        flSetupContainer.setOnClickListener(v -> {
        });
    }

    private void openTapTalkSocketWhenNeeded() {
        if (TapTalk.getTapTalkSocketConnectionMode(instanceKey) == TapTalk.TapTalkSocketConnectionMode.CONNECT_IF_NEEDED &&
                !TapTalk.isConnected(instanceKey) && TapTalk.isForeground) {
            TapTalk.connect(instanceKey, new TapCommonListener() {
            });
        }
    }

    private void closeTapTalkSocketWhenNeeded() {
        if (TapTalk.getTapTalkSocketConnectionMode(instanceKey) == TapTalk.TapTalkSocketConnectionMode.CONNECT_IF_NEEDED
                && TapTalk.isConnected(instanceKey)) {
            TapTalk.disconnect(instanceKey);
        }
    }

    private void reloadProfilePicture() {
        // TODO: 7 May 2019 CHECK IF PROFILE IS HIDDEN
        TAPUserModel user = TAPChatManager.getInstance(instanceKey).getActiveUser();
        if (null != user && null != user.getImageURL()
                && !user.getImageURL().getThumbnail().isEmpty()) {
            if (null != getActivity()) {
                getActivity().runOnUiThread(() -> {
                    glide.load(user.getImageURL().getThumbnail()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Show initial
                            if (null != getActivity()) {
                                getActivity().runOnUiThread(() -> {
                                    ImageViewCompat.setImageTintList(civMyAvatarImage, ColorStateList.valueOf(TAPUtils.getRandomColor(getContext(), user.getFullname())));
                                    if (null != getContext()) {
                                        civMyAvatarImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.tap_bg_circle_9b9b9b));
                                    }
                                });
                            }
                            tvMyAvatarLabel.setText(TAPUtils.getInitials(user.getFullname(), 2));
                            tvMyAvatarLabel.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(civMyAvatarImage);
                    ImageViewCompat.setImageTintList(civMyAvatarImage, null);
                    civMyAvatarImage.setVisibility(View.VISIBLE);
                    tvMyAvatarLabel.setVisibility(View.GONE);
                });
            }
        }
        else if (null != user) {
            // Show initial
            if (null != getActivity()) {
                getActivity().runOnUiThread(() -> {
                    glide.clear(civMyAvatarImage);
                    ImageViewCompat.setImageTintList(civMyAvatarImage, ColorStateList.valueOf(TAPUtils.getRandomColor(getContext(), user.getFullname())));
                    if (null != getContext()) {
                        civMyAvatarImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.tap_bg_circle_9b9b9b));
                    }
                    civMyAvatarImage.setVisibility(View.VISIBLE);
                    tvMyAvatarLabel.setText(TAPUtils.getInitials(user.getFullname(), 2));
                    tvMyAvatarLabel.setVisibility(View.VISIBLE);
                });
            }
        }
        else {
            // Hide profile picture
            if (null != getActivity()) {
                getActivity().runOnUiThread(() -> {
                    glide.clear(civMyAvatarImage);
                    civMyAvatarImage.setVisibility(View.GONE);
                    tvMyAvatarLabel.setVisibility(View.GONE);
                });
            }
        }
    }

    private void showSearchChat() {
        TAPUtils.animateClickButton(cvButtonSearch, 0.97f);
        TAPChatManager.getInstance(instanceKey).triggerSearchChatBarTapped(activity, mainRoomListFragment);
    }

    private void openMyAccountActivity() {
        TAPChatManager.getInstance(instanceKey).triggerTapTalkAccountButtonTapped(activity);
    }

    private void openNewChatActivity() {
        TAPChatManager.getInstance(instanceKey).triggerNewChatButtonTapped(activity);
    }

    private void viewLoadedSequence() {
        if (TAPRoomListViewModel.isShouldNotLoadFromAPI(instanceKey) && null != TAPChatManager.getInstance(instanceKey).getActiveUser()) {
            // Load room list from database if app is on foreground
            if (vm.getRoomList().isEmpty()) {
                TAPDataManager.getInstance(instanceKey).getRoomList(true, dbListener);
            }
        }
        else if (null != TAPChatManager.getInstance(instanceKey).getActiveUser()) {
            // Run full cycle if app is on background or on first open
            // TODO: 18 Feb 2020 DATABASE FIRST QUERY CALLED TWICE WHEN CLOSING APP (NOT KILLED)
            runFullRefreshSequence();
        }
        else if (TapTalk.checkTapTalkInitialized() && TapTalk.isAuthenticated(instanceKey)) {
            // Clear data when refresh token is expired
            TapTalk.clearAllTapTalkData(instanceKey);
            List<TapListener> listeners = getTapTalkListeners(instanceKey);
            if (listeners != null && !listeners.isEmpty()) {
                for (TapListener listener : listeners) {
                    listener.onTapTalkRefreshTokenExpired();
                }
            }
        }
        else if (null == TAPChatManager.getInstance(instanceKey).getActiveUser()) {
            // Show setup failed if active user is null
            flSetupContainer.setVisibility(View.VISIBLE);
            showChatRoomSetupFailed();
            if (BuildConfig.DEBUG && null != activity) {
                if (null == userNullErrorDialog) {
                    userNullErrorDialog = new TapTalkDialog(new TapTalkDialog.Builder(activity)
                        .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                        .setTitle(getString(R.string.tap_error))
                        .setMessage(getString(R.string.tap_error_active_user_is_null))
                        .setCancelable(false)
                        .setPrimaryButtonTitle(getString(R.string.tap_ok)));
                }
                userNullErrorDialog.show();
            }
            Log.e(TAG, getString(R.string.tap_error_active_user_is_null));
        }
    }

    private void getDatabaseAndAnimateResult() {
        TAPDataManager.getInstance(instanceKey).getRoomList(true, dbAnimatedListener);
    }

    private void runFullRefreshSequence() {
        if (!vm.getRoomList().isEmpty()) {
            // Check and update unread badge before updating view if recycler is not empty
            TAPDataManager.getInstance(instanceKey).getRoomList(TAPChatManager.getInstance(instanceKey).getSaveMessages(), true, dbListener);
        }
        else {
            // Update unread badge after view is updated if recycler is empty
            TAPDataManager.getInstance(instanceKey).getRoomList(TAPChatManager.getInstance(instanceKey).getSaveMessages(), false, dbListener);
        }
        TAPGroupManager.Companion.getInstance(instanceKey).setRefreshRoomList(false);
    }

    private void fetchDataFromAPI() {
        if (vm.isDoneFirstApiSetup()) {
            // Call to refresh new messages
            TAPDataManager.getInstance(instanceKey).getNewAndUpdatedMessage(roomListView);
        }
        else {
            // Call on first load
            vm.setFetchingMessageListAndUnread(true);
            TAPDataManager.getInstance(instanceKey).getMessageRoomListAndUnread(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(), roomListView);
        }
    }

    private void reloadLocalDataAndUpdateUILogic(boolean isAnimated) {
        if (null == getContext() || activity == null || activity.isFinishing()) {
            return;
        }
        activity.runOnUiThread(() -> {
            if (null != adapter && vm.getRoomList().isEmpty()) {
                // Room list is empty
                llRoomEmpty.setVisibility(View.VISIBLE);
                flSetupContainer.setVisibility(View.GONE);
            }
            else if (null != adapter && (!TAPRoomListViewModel.isShouldNotLoadFromAPI(instanceKey) || isAnimated) && TAPNotificationManager.getInstance(instanceKey).isRoomListAppear()) {
                // Show room list on first open and animate
                adapter.addRoomList(vm.getRoomList());
                rvContactList.scrollToPosition(0);
                llRoomEmpty.setVisibility(View.GONE);
                if (vm.isDoneFirstApiSetup()) {
                    TAPDataManager.getInstance(instanceKey).setRoomListSetupFinished();
                    showChatRoomSetupSuccess();
                }
            }
            else if (null != adapter && TAPRoomListViewModel.isShouldNotLoadFromAPI(instanceKey)) {
                // Update room list without animating
                adapter.setItems(vm.getRoomList(), false);
                llRoomEmpty.setVisibility(View.GONE);
                flSetupContainer.setVisibility(View.GONE);
            }
            showNewChatButton();

            if (!TAPRoomListViewModel.isShouldNotLoadFromAPI(instanceKey)) {
                fetchDataFromAPI();
            }
        });
    }

    private void processMessageFromSocket(TAPMessageModel message) {
        rvContactList.post(() -> {
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.runOnUiThread(() -> {
                String messageRoomID = message.getRoom().getRoomID();
                TAPRoomListModel roomList = vm.getRoomPointer().get(messageRoomID);

                if (null != roomList && null != message.getIsHidden() && !message.getIsHidden()) {
                    // Received message in an existing room list
                    roomList.setLastMessageTimestampWithLong(message.getCreated());
                    TAPMessageModel roomLastMessage = roomList.getLastMessage();

                    if (roomLastMessage.getLocalID().equals(message.getLocalID()) && null != activity) {
                        // Update room list's last message data
                        roomLastMessage.updateValue(message);
                        activity.runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(roomList)));
                    }
                    else if (roomLastMessage.getCreated() < message.getCreated()) {
                        int oldPos = vm.getRoomList().indexOf(roomList);
                        if (LEAVE_ROOM.equals(message.getAction()) && vm.getMyUserID().equals(message.getUser().getUserID())) {
                            // Remove room from list
                            vm.getRoomList().remove(roomList);
                            vm.getRoomPointer().remove(roomList.getLastMessage().getRoom().getRoomID());
                            activity.runOnUiThread(() -> adapter.notifyItemRemoved(oldPos));
                        }
                        else {
                            // Update room list's last message with the new message from socket
                            roomList.setLastMessage(message);

                            // Add unread count by 1 if sender is not self
                            if (!roomList.getLastMessage().getUser().getUserID().equals(
                                    TAPChatManager.getInstance(instanceKey)
                                            .getActiveUser().getUserID())) {
                                roomList.setNumberOfUnreadMessages(roomList.getNumberOfUnreadMessages() + 1);
                            }

                            // Show mention badge if user is mentioned
                            if (TAPUtils.isActiveUserMentioned(roomList.getLastMessage(),
                                    TAPChatManager.getInstance(instanceKey).getActiveUser())) {
                                roomList.setNumberOfUnreadMentions(roomList.getNumberOfUnreadMentions() + 1);
                            }

                            // Move room to top
                            int index;
                            if (!TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().contains(messageRoomID)) {
                                vm.getRoomList().remove(roomList);
                                index = TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().size();
                                vm.getRoomList().add(index, roomList);
                            }
                            else {
                                index = oldPos;
                            }

                            if (null != activity) {
                                activity.runOnUiThread(() -> {
                                    llRoomEmpty.setVisibility(View.GONE);
                                    adapter.notifyItemChanged(oldPos);
                                    if (index != oldPos) {
                                        adapter.notifyItemMoved(oldPos, index);
                                    }
                                    // Scroll to top
                                    if (llm.findFirstCompletelyVisibleItemPosition() == 0)
                                        rvContactList.scrollToPosition(0);
                                });
                            }
                        }
                    }
                }
                else if (null != activity && null != message.getIsHidden() && !message.getIsHidden()) {
                    // Received message in a new room list
                    TAPRoomListModel newRoomList = TAPRoomListModel.buildWithLastMessage(message);
                    if (!newRoomList.getLastMessage().getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                        newRoomList.setNumberOfUnreadMessages(1);
                    }

                    if (TAPUtils.isActiveUserMentioned(newRoomList.getLastMessage(), TAPChatManager.getInstance(instanceKey).getActiveUser())) {
                        newRoomList.setNumberOfUnreadMentions(1);
                    }

                    int index;
                    if (!TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().contains(messageRoomID)) {
                        vm.getRoomList().remove(newRoomList);
                        index = TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().size();
                    }
                    else {
                        index = TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().indexOf(messageRoomID);
                    }
                    vm.addRoomPointer(newRoomList);
                    vm.getRoomList().add(index, newRoomList);
                    activity.runOnUiThread(() -> {
                        llRoomEmpty.setVisibility(View.GONE);
                        if (adapter.getItems().isEmpty()) {
                            adapter.addItem(newRoomList);
                        }
                        adapter.notifyItemInserted(index);

                        if (llm.findFirstCompletelyVisibleItemPosition() == 0)
                            rvContactList.scrollToPosition(0);
                    });
                }

                if (null != roomList && message.getType() == TYPE_SYSTEM_MESSAGE &&
                    null != message.getAction() &&
                    (message.getAction().equals(UPDATE_ROOM) || message.getAction().equals(UPDATE_USER) || message.getAction().equals(DELETE_USER))
                ) {
                    // Update room details
                    activity.runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(roomList)));
                }
                calculateBadgeCount();
            });
        });
    }

    private void updateProfilePictureFromSystemMessage(TAPMessageModel message) {
        TAPUserModel activeUser = TAPChatManager.getInstance(instanceKey).getActiveUser();
        String activeUserID = activeUser.getUserID();
        if (message.getType() == TYPE_SYSTEM_MESSAGE &&
            null != message.getAction() &&
            message.getAction().equals(UPDATE_USER) &&
            null != message.getUser() &&
            null != message.getUser().getUpdated() &&
            null != activeUser.getUpdated() &&
            message.getUser().getUpdated() > activeUser.getUpdated() &&
            message.getUser().getUserID().equals(activeUserID) &&
            civMyAvatarImage.getVisibility() == View.VISIBLE
        ) {
            // Update user avatar
            activeUser.updateValue(message.getUser());
            TAPDataManager.getInstance(instanceKey).saveActiveUser(activeUser);
            reloadProfilePicture();
        }
    }

    private void showSelectionActionBar() {
        vm.setSelecting(true);
        clButtonSearch.setElevation(0);
        clButtonSearch.setVisibility(View.INVISIBLE);
    }

    private void hideSelectionActionBar() {
        vm.setSelecting(false);
        clButtonSearch.setElevation(TAPUtils.dpToPx(2));
        clButtonSearch.setVisibility(View.VISIBLE);
    }

    public void cancelSelection() {
        vm.getSelectedRooms().clear();
        adapter.notifyDataSetChanged();
        hideSelectionActionBar();
    }

    private void showNewChatButton() {
        if (!TapUI.getInstance(instanceKey).isNewChatButtonVisible() || ivButtonNewChat.getVisibility() == View.VISIBLE) {
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
        if (null == activity || activity.isFinishing() || !vm.getRoomPointer().containsKey(roomID) || null == typingModel.getUser()) {
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
                }
                else {
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
            }
            else {
                roomListModel.removeTypingUser(typingModel.getUser().getUserID());
            }

            adapter.notifyItemChanged(vm.getRoomList().indexOf(roomListModel));
        });
    }

    private void showChatRoomSettingUp() {
        if (activity == null || activity.isFinishing() || !TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable()) {
            return;
        }
        activity.runOnUiThread(() -> {
            ivSetupChat.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_setting_up_grey));
            ivSetupChatLoading.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_loading_progress_circle_white));
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

    private void showDeleteRoomLoading() {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        activity.runOnUiThread(() -> {
            ivSetupChat.setImageDrawable(null);
            ivSetupChatLoading.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_loading_progress_circle_white));
            ivSetupChat.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconRoomListSettingUp));
            ivSetupChatLoading.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconLoadingProgressPrimary));
            tvSetupChat.setText(R.string.tap_deleting_conversation_dots);
            tvSetupChatDescription.setText(getString(R.string.tap_chat_room_setting_up_description));
            TAPUtils.rotateAnimateInfinitely(activity, ivSetupChatLoading);

            tvSetupChatDescription.setVisibility(View.VISIBLE);
            llRetrySetup.setVisibility(View.GONE);
            flSetupContainer.setVisibility(View.VISIBLE);

            llRetrySetup.setOnClickListener(null);

            hideNewChatButton();
        });
    }

    private void hideDeleteRoomLoading() {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        activity.runOnUiThread(() -> {
            showNewChatButton();
            flSetupContainer.setVisibility(View.GONE);
        });
    }

    private void showChatRoomSetupSuccess() {
        if (activity == null || activity.isFinishing() || !TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable()) {
            return;
        }
        activity.runOnUiThread(() -> {
            ivSetupChat.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_setup_success_green));
            ivSetupChatLoading.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_loading_progress_full_circle_red));
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
        if (activity == null || activity.isFinishing() || !TAPDataManager.getInstance(instanceKey).checkAccessTokenAvailable()) {
            return;
        }
        activity.runOnUiThread(() -> {
            ivSetupChat.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_setup_failed_red));
            ivSetupChatLoading.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.tap_ic_loading_progress_full_circle_red));
            ivSetupChat.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconRoomListSetupFailure));
            ivSetupChatLoading.setColorFilter(ContextCompat.getColor(activity, R.color.tapIconRoomListSetupFailure));
            ivSetupChatLoading.clearAnimation();
            tvSetupChat.setText(getString(R.string.tap_chat_room_setup_failed));

            tvSetupChatDescription.setVisibility(View.GONE);
            llRetrySetup.setVisibility(View.VISIBLE);

            llRetrySetup.setOnClickListener(view -> {
                TAPUtils.animateClickButton(llRetrySetup, 0.95f);
                if (null == TAPChatManager.getInstance(instanceKey).getActiveUser()) {
                    return;
                }
                vm.setFetchingMessageListAndUnread(true);
                TAPDataManager.getInstance(instanceKey).getMessageRoomListAndUnread(
                        TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID(), roomListView);
            });
        });
    }

    private void getRoomIdsWithState(boolean isHandleDeleteRoomEnabled) {
        TAPDataManager.getInstance(instanceKey).getRoomIdsWithState(new TAPDefaultDataView<>() {
            @Override
            public void onSuccess(TapGetRoomIdsWithStateResponse response) {
                if (response.getUnreadRoomIDs() != null) {
                    TAPDataManager.getInstance(instanceKey).saveUnreadRoomIDs(new ArrayList<>(response.getUnreadRoomIDs()));
                    for (String id : response.getUnreadRoomIDs()) {
                        updateRoomUnreadMark(id, true);
                        if (activity != null && !activity.isFinishing()) {
                            activity.runOnUiThread(() -> {
                                adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(id)));
                            });
                        }
                    }
                    calculateBadgeCount();
                }
                if (response.getMutedRooms() != null) {
                    HashMap<String, Long> mutedRooms = TAPDataManager.getInstance(instanceKey).getMutedRoomIDs();
                    for (TapMutedRoomListModel mutedRoom : response.getMutedRooms()) {
                        mutedRooms.put(mutedRoom.getRoomID(), mutedRoom.getExpiredAt());
                        updateMutedRooms(mutedRoom.getRoomID(), true);
                        if (activity != null && !activity.isFinishing()) {
                            activity.runOnUiThread(() -> {
                                adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(mutedRoom.getRoomID())));
                            });
                        }
                    }
                    TAPDataManager.getInstance(instanceKey).saveMutedRoomIDs(mutedRooms);
                }
                if (response.getPinnedRoomIDs() != null) {
                    TAPDataManager.getInstance(instanceKey).savePinnedRoomIDs(new ArrayList<>(response.getPinnedRoomIDs()));
                    for (String pinnedRoom : response.getPinnedRoomIDs()) {
                        updatePinnedRooms(pinnedRoom, true);
                        if (activity != null && !activity.isFinishing()) {
                            activity.runOnUiThread(() -> {
                                adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(pinnedRoom)));
                            });
                        }
                    }
                }
                if (isHandleDeleteRoomEnabled) {
                    if (response.getClearedRooms() != null) {
                        new Thread(() -> {
                            for (TapMutedRoomListModel clearedRoom : response.getClearedRooms()) {
                                if (clearedRoom.getExpiredAt() != null && TAPDataManager.getInstance(instanceKey).getLastRoomMessageDeleteTime() < clearedRoom.getExpiredAt()) {
                                    TAPDataManager.getInstance(instanceKey).getRoomMediaMessageBeforeTimestamp(clearedRoom.getRoomID(), clearedRoom.getExpiredAt(), new TAPDatabaseListener<>() {
                                        @Override
                                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                                            super.onSelectFinished(entities);
                                            for (TAPMessageEntity message : entities) {
                                                TAPDataManager.getInstance(instanceKey).deletePhysicalFile(message);
                                            }
                                            TAPDataManager.getInstance(instanceKey).deleteRoomMessageBeforeTimestamp(clearedRoom.getRoomID(), clearedRoom.getExpiredAt(), new TAPDatabaseListener<TAPMessageEntity>() {
                                            });
                                        }
                                    });
                                }
                            }
                            TAPDataManager.getInstance(instanceKey).saveLastRoomMessageDeleteTime();
                        }).start();
                    }
                }
                else {
                    TAPDataManager.getInstance(instanceKey).saveLastRoomMessageDeleteTime();
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                super.onError(error);
                calculateBadgeCount();
            }

            @Override
            public void onError(String errorMessage) {
                super.onError(errorMessage);
                calculateBadgeCount();
            }
        });
    }

    private void getBlockedUserIds() {
        TAPDataManager.getInstance(instanceKey).getBlockedUserIds(blockedUserIdsView);
    }

    private final TAPDefaultDataView<TapGetUnreadRoomIdsResponse> blockedUserIdsView = new TAPDefaultDataView<>() {
        @Override
        public void onSuccess(TapGetUnreadRoomIdsResponse response) {
            super.onSuccess(response);
            TAPDataManager.getInstance(instanceKey).saveBlockedUserIds(new ArrayList<>(response.getUnreadRoomIDs()));
        }
    };

    private final TAPDefaultDataView<TAPGetRoomListResponse> roomListView = new TAPDefaultDataView<TAPGetRoomListResponse>() {
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
            if (!response.getMessages().isEmpty()) {
                List<TAPMessageEntity> tempMessage = new ArrayList<>();
                List<TAPMessageModel> deliveredMessages = new ArrayList<>();
                List<String> userIds = new ArrayList<>();
                TAPMessageModel updateProfileSystemMessage = null;
                List<TAPUserModel> userModels = new ArrayList<>();
                for (HashMap<String, Object> messageMap : response.getMessages()) {
                    try {
                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                        if (message != null) {
                            TAPMessageEntity entity = TAPMessageEntity.fromMessageModel(message);
                            tempMessage.add(entity);

                            // Save undelivered messages to list
                            if (null == message.getIsDelivered() || (null != message.getIsDelivered() && !message.getIsDelivered())) {
                                deliveredMessages.add(message);
                            }

                            // Update Contact
                            if (message.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID())) {
                                // User is self, get other user data from API
                                userIds.add(TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(message.getRoom().getRoomID()));
                            }
                            else {
                                // Save user data to contact manager
                                userModels.add(message.getUser());
                            }

                            if (null != message.getIsDeleted() && message.getIsDeleted()) {
                                TAPDataManager.getInstance(instanceKey).deletePhysicalFile(entity);
                            }

                            if (message.getType() == TYPE_SYSTEM_MESSAGE &&
                                null != message.getAction() &&
                                message.getAction().equals(UPDATE_USER) &&
                                (null == updateProfileSystemMessage || updateProfileSystemMessage.getCreated() < message.getCreated())
                            ) {
                                // Store update profile system message
                                updateProfileSystemMessage = message;
                            }

                            TAPUtils.handleReceivedSystemMessage(instanceKey, message);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                TAPContactManager.getInstance(instanceKey).updateUserData(userModels);

                if (null != updateProfileSystemMessage) {
                    // Update room detail if update room system message exists in API result
                    updateProfilePictureFromSystemMessage(updateProfileSystemMessage);
                }

                // Update status to delivered
                if (!deliveredMessages.isEmpty()) {
                    TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDelivered(deliveredMessages);
                }

                // Get updated other user data from API
                if (!userIds.isEmpty()) {
                    TAPDataManager.getInstance(instanceKey).getMultipleUsersByIdFromApi(userIds, getMultipleUserView);
                }

                // Save message to database
                TAPDataManager.getInstance(instanceKey).insertToDatabase(tempMessage, false, new TAPDatabaseListener<>() {
                    @Override
                    public void onInsertFinished() {
                        // Reload newest room list from database
                        getDatabaseAndAnimateResult();
                    }
                });
            }
            else {
                reloadLocalDataAndUpdateUILogic(true);
            }

            if (vm.isFetchingMessageListAndUnread() && !vm.isDoneFirstApiSetup()) {
                vm.setDoneFirstApiSetup(true);
                TAPDataManager.getInstance(instanceKey).setRoomListSetupFinished();
                showChatRoomSetupSuccess();
            }

            vm.setFetchingMessageListAndUnread(false);

            getRoomIdsWithState(true);
            getBlockedUserIds();
            TAPRoomListViewModel.setShouldNotLoadFromAPI(instanceKey, true);
        }

        @Override
        public void onError(TAPErrorModel error) {
            onError(error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            if (!vm.isDoneFirstApiSetup()) {
                showChatRoomSetupFailed();
            }
            else {
                flSetupContainer.setVisibility(View.GONE);
                showNewChatButton();
            }

            vm.setFetchingMessageListAndUnread(false);
        }
    };

    private final TAPDefaultDataView<TAPGetMultipleUserResponse> getMultipleUserView = new TAPDefaultDataView<TAPGetMultipleUserResponse>() {
        @Override
        public void onSuccess(TAPGetMultipleUserResponse response) {
            if (null == response || response.getUsers().isEmpty()) {
                return;
            }
            new Thread(() -> TAPContactManager.getInstance(instanceKey).updateUserData(response.getUsers())).start();
        }
    };

    private final TAPDatabaseListener<TAPMessageEntity> dbListener = new TAPDatabaseListener<>() {
        @Override
        public void onSelectFinishedWithUnreadCount(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
            List<TAPRoomListModel> messageModels = new ArrayList<>();
            vm.getRoomPointer().clear();
            HashMap<Integer, TAPRoomListModel> pinnedRooms = new HashMap<>();
            int count = 0; // FIXME Count to load room list every 10 items
            int limit = 25;
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPMessageModel.fromMessageEntity(entity);
                TAPRoomListModel roomModel = TAPRoomListModel.buildWithLastMessage(model);
                roomModel.setMuted(TAPDataManager.getInstance(instanceKey).getMutedRoomIDs().containsKey(entity.getRoomID()));
                roomModel.setPinned(TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().contains(entity.getRoomID()));
                if (roomModel.isPinned()) {
                    pinnedRooms.put(TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().indexOf(entity.getRoomID()), roomModel);
                }else {
                    messageModels.add(roomModel);
                }
                vm.addRoomPointer(roomModel);
                TAPRoomListModel roomList = vm.getRoomPointer().get(entity.getRoomID());
                if (null != roomList && null != unreadMap) {
                    Integer unreadCount = unreadMap.get(entity.getRoomID());
                    if (null != unreadCount) {
                        roomList.setNumberOfUnreadMessages(unreadCount);
                    }
                }
                if (null != roomList && null != mentionMap) {
                    Integer mentionCount = mentionMap.get(entity.getRoomID());
                    if (null != mentionCount) {
                        roomList.setNumberOfUnreadMentions(mentionCount);
                    }
                }
                if (++count % limit == 0) {
                    vm.setRoomList(messageModels);
                    if (activity != null && !activity.isFinishing()) {
                        activity.runOnUiThread(() -> adapter.setItems(vm.getRoomList()));
                    }
                    limit = limit * 2;
                }
            }

            for (int i = 0; i < pinnedRooms.size(); i++) {
                messageModels.add(i, pinnedRooms.get(i));
            }
            vm.setRoomList(messageModels);
            reloadLocalDataAndUpdateUILogic(false);
            getRoomIdsWithState(false);
            getBlockedUserIds();
        }

        @Override
        public void onCountedUnreadCount(String roomID, int unreadCount, int mentionCount) {
            try {
                TAPRoomListModel roomList = vm.getRoomPointer().get(roomID);
                if (null != activity && null != roomList) {
                    roomList.setNumberOfUnreadMessages(unreadCount);
                    roomList.setNumberOfUnreadMentions(mentionCount);
                    if (activity != null && !activity.isFinishing()) {
                        activity.runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID))));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
            List<TAPRoomListModel> messageModels = new ArrayList<>();
            vm.getRoomPointer().clear();
            HashMap<Integer, TAPRoomListModel> pinnedRooms = new HashMap<>();
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPMessageModel.fromMessageEntity(entity);
                TAPRoomListModel roomListModel = TAPRoomListModel.buildWithLastMessage(model);
                roomListModel.setMuted(TAPDataManager.getInstance(instanceKey).getMutedRoomIDs().containsKey(entity.getRoomID()));
                roomListModel.setPinned(TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().contains(entity.getRoomID()));
                Integer unreadCount = unreadMap.get(entity.getRoomID());
                if (unreadCount != null) {
                    roomListModel.setNumberOfUnreadMessages(unreadCount);
                }
                Integer mentionCount = mentionMap.get(entity.getRoomID());
                if (mentionCount != null) {
                    roomListModel.setNumberOfUnreadMentions(mentionCount);
                }
                vm.addRoomPointer(roomListModel);
                if (roomListModel.isPinned()) {
                    pinnedRooms.put(TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().indexOf(entity.getRoomID()), roomListModel);
                }
                else {
                    messageModels.add(roomListModel);
                }
            }

            for (int i = 0; i < pinnedRooms.size(); i++) {
                messageModels.add(i, pinnedRooms.get(i));
            }
            vm.setRoomList(messageModels);
            reloadLocalDataAndUpdateUILogic(false);
            calculateBadgeCount();
        }
    };

    private final TAPDatabaseListener<TAPMessageEntity> dbAnimatedListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
            List<TAPRoomListModel> messageModels = new ArrayList<>();
            HashMap<Integer, TAPRoomListModel> pinnedRooms = new HashMap<>();
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPMessageModel.fromMessageEntity(entity);
                TAPRoomListModel roomListModel = TAPRoomListModel.buildWithLastMessage(model);
                roomListModel.setMuted(TAPDataManager.getInstance(instanceKey).getMutedRoomIDs().containsKey(entity.getRoomID()));
                roomListModel.setPinned(TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().contains(entity.getRoomID()));
                Integer unreadCount = unreadMap.get(entity.getRoomID());
                if (unreadCount != null) {
                    roomListModel.setNumberOfUnreadMessages(unreadCount);
                }
                Integer mentionCount = mentionMap.get(entity.getRoomID());
                if (mentionCount != null) {
                    roomListModel.setNumberOfUnreadMentions(mentionCount);
                }
                vm.addRoomPointer(roomListModel);
                if (roomListModel.isPinned()) {
                    pinnedRooms.put(TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().indexOf(entity.getRoomID()), roomListModel);
                }
                else {
                    messageModels.add(roomListModel);
                }
            }
            for (int i = 0; i < pinnedRooms.size(); i++) {
                messageModels.add(i, pinnedRooms.get(i));
            }
            vm.setRoomList(messageModels);
            reloadLocalDataAndUpdateUILogic(true);
            calculateBadgeCount();
        }
    };

    OnMoveAndSwipeListener onMoveAndSwipeListener = new OnMoveAndSwipeListener() {
        @Override
        public void onReadOrUnreadItem(int position) {
            TAPRoomListModel room = vm.getRoomList().get(position);
            String roomId = room.getLastMessage().getRoom().getRoomID();
            if (room.isMarkedAsUnread() || room.getNumberOfUnreadMessages() > 0) {
                // read button
                TapCoreMessageManager.getInstance(instanceKey).markAllMessagesInRoomAsRead(roomId);
            }
            else {
                // unread button
                TapCoreRoomListManager.getInstance(instanceKey).markChatRoomAsUnread(roomId, null);
            }
        }

        @Override
        public void onMuteOrUnmuteItem(int position) {
            TAPRoomListModel room = vm.getRoomList().get(position);
            String roomId = room.getLastMessage().getRoom().getRoomID();
            if (room.isMuted()) {
                TapCoreRoomListManager.getInstance(instanceKey).unmuteChatRoom(roomId, muteRoomListener(position, roomId));
            }
            else {
                TapMuteBottomSheet muteBottomSheet = new TapMuteBottomSheet(roomId, position, muteListener);
                muteBottomSheet.show(getChildFragmentManager(), "");
            }
        }

        @Override
        public void onPinOrUnpinItem(int position) {
            TAPRoomListModel room = vm.getRoomList().get(position);
            String roomId = room.getLastMessage().getRoom().getRoomID();
            if (room.isPinned()) {
                // unpin button
                TapCoreRoomListManager.getInstance(instanceKey).unpinChatRoom(roomId, pinRoomListener(position, roomId));
            }
            else {
                // pin button
                TapCoreRoomListManager.getInstance(instanceKey).pinChatRoom(roomId, pinRoomListener(position, roomId));
            }
        }

        @Override
        public void onDeleteItem(int position) {
            TAPRoomListModel room = vm.getRoomList().get(position);
            String roomId = room.getLastMessage().getRoom().getRoomID();
            String title;
            String message;
            if (TAPDataManager.getInstance(instanceKey).getPinnedRoomIDs().contains(roomId)) {
                title = getString(R.string.tap_unpin_and_delete_chat);
                message = getString(R.string.tap_sure_unpin_delete_conversation);
            }
            else {
                title = getString(R.string.tap_delete_chat);
                message = getString(R.string.tap_sure_delete_conversation);
            }
            new TapTalkDialog(new TapTalkDialog.Builder(activity)
                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPrimaryButtonTitle(getString(R.string.tap_delete_for_me))
                    .setPrimaryButtonListener(v -> {
                        showDeleteRoomLoading();
                        TapCoreChatRoomManager.getInstance(instanceKey).deleteAllChatRoomMessages(roomId, new TapCommonListener() {
                            @Override
                            public void onSuccess(String successMessage) {
                                super.onSuccess(successMessage);
                                activity.runOnUiThread(() -> {
                                    hideDeleteRoomLoading();
                                    updatePinnedRooms(roomId, false);
                                    updateRoomUnreadMark(roomId, false);
                                });
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {
                                super.onError(errorCode, errorMessage);
                                activity.runOnUiThread(() -> {
                                    hideDeleteRoomLoading();
                                    new TapTalkDialog(new TapTalkDialog.Builder(activity)
                                        .setDialogType(TapTalkDialog.DialogType.DEFAULT)
                                        .setTitle(getString(R.string.tap_fail_delete_chatroom))
                                        .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                        .setPrimaryButtonListener(v -> {})
                                        .setCancelable(false))
                                        .show();
                                });
                            }
                        });
                    })
                    .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                    .setSecondaryButtonListener(v -> {}))
                    .show();
        }
    };

    TAPGeneralListener<TapMutedRoomListModel> muteListener = new TAPGeneralListener<>() {
        @Override
        public void onClick(int position, TapMutedRoomListModel item) {
            super.onClick(position, item);
            if (item == null) {
                return;
            }
            if (item.getExpiredAt() != null && item.getExpiredAt() == -1) {
                // unmute
                TapCoreRoomListManager.getInstance(instanceKey).unmuteChatRoom(item.getRoomID(), muteRoomListener(position, item.getRoomID()));
            }
            else {
                // mute
                TapCoreRoomListManager.getInstance(instanceKey).muteChatRoom(item.getRoomID(), item.getExpiredAt(), muteRoomListener(position, item.getRoomID()));
            }
        }
    };

    private TapCommonListener muteRoomListener(int position, String roomId) {
        return new TapCommonListener() {
            @Override
            public void onSuccess(String successMessage) {
                super.onSuccess(successMessage);
                boolean isMuted = TAPDataManager.getInstance(instanceKey).getMutedRoomIDs().containsKey(roomId);
                updateMutedRooms(roomId, isMuted);
                if (null != getActivity()) {
                    getActivity().runOnUiThread(() -> adapter.notifyItemChanged(position));
                }
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                super.onError(errorCode, errorMessage);
                showErrorDialog(errorMessage);
            }
        };
    }

    private TapCommonListener pinRoomListener(int position, String roomId) {
        return new TapCommonListener() {
            @Override
            public void onError(String errorCode, String errorMessage) {
                super.onError(errorCode, errorMessage);
                showErrorDialog(errorMessage);
            }
        };
    }

    private void showErrorDialog(String errorMessage) {
        new TapTalkDialog(new TapTalkDialog.Builder(activity)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(getString(R.string.tap_error))
                .setMessage(errorMessage)
                .setCancelable(false)
                .setPrimaryButtonTitle(getString(R.string.tap_ok)))
                .show();
    }

    private void updateRoomUnreadMark(String roomId, boolean isMarkAsUnread) {
        TAPRoomListModel room = vm.getRoomPointer().get(roomId);
        if (room != null) {
            room.setMarkedAsUnread(isMarkAsUnread);
            if (!isMarkAsUnread) {
                room.setNumberOfUnreadMessages(0);
            }
        }
    }

    private void updateMutedRooms(String roomId, boolean isMuted) {
        TAPRoomListModel room = vm.getRoomPointer().get(roomId);
        if (room != null) {
            room.setMuted(isMuted);
        }
    }

    private void updatePinnedRooms(String roomId, boolean isPinned) {
        TAPRoomListModel room = vm.getRoomPointer().get(roomId);
        if (room != null) {
            room.setPinned(isPinned);
        }
    }


    private void updateQueryRoomListFromBackground() {
        if (TAPDataManager.getInstance(instanceKey).isNeedToQueryUpdateRoomList()) {
            runFullRefreshSequence();
            TAPDataManager.getInstance(instanceKey).setNeedToQueryUpdateRoomList(false);
        }
    }

    private void addNetworkListener() {
        TAPNetworkStateManager.getInstance(instanceKey).addNetworkListener(networkListener);
    }

    private void removeNetworkListener() {
        TAPNetworkStateManager.getInstance(instanceKey).removeNetworkListener(networkListener);
    }

    private void updateUnreadCountPerRoom(String roomID) {
        new Thread(() -> {
            TAPRoomListModel room = vm.getRoomPointer().get(roomID);
            if (null != getActivity() && null != room) {
                Integer roomUnreadList = TAPMessageStatusManager.getInstance(instanceKey).getUnreadList().get(roomID);
                if (null != roomUnreadList) {
                    if (roomUnreadList <= room.getNumberOfUnreadMessages()) {
                        // Subtract unread count from room
                        room.setNumberOfUnreadMessages(room.getNumberOfUnreadMessages() - roomUnreadList);
                    }
                    else {
                        // Set room unread count to 0
                        room.setNumberOfUnreadMessages(0);
                    }
                    TAPMessageStatusManager.getInstance(instanceKey).clearUnreadListPerRoomID(roomID);
                }
                Integer roomUnreadMention = TAPMessageStatusManager.getInstance(instanceKey).getUnreadMention().get(roomID);
                if (null != roomUnreadMention) {
                    if (roomUnreadMention <= room.getNumberOfUnreadMentions()) {
                        // Subtract unread mention from room
                        room.setNumberOfUnreadMentions(room.getNumberOfUnreadMentions() - roomUnreadMention);
                    }
                    else {
                        // Set room unread mention to 0
                        room.setNumberOfUnreadMentions(0);
                    }
                    TAPMessageStatusManager.getInstance(instanceKey).clearUnreadMentionPerRoomID(roomID);
                }
                getActivity().runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(room)));
            }
            calculateBadgeCount();
        }).start();
    }

    private final BroadcastReceiver roomListBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent.getAction()) {
                return;
            }
            switch (intent.getAction()) {
                case REFRESH_TOKEN_RENEWED:
                    if (null != userNullErrorDialog) {
                        userNullErrorDialog.dismiss();
                    }
                    // Token refreshed
                    initViewModel();
                    initView();
                    viewLoadedSequence();
                    break;
                case CLEAR_ROOM_LIST:
                    // Logged out
                    initViewModel();
                    initView();
                    viewLoadedSequence();
                    break;
                case RELOAD_PROFILE_PICTURE:
                    // Reload profile picture
                    reloadProfilePicture();
                    break;
                case OPEN_CHAT:
                    // Open Chat
                    TAPMessageModel messageModel = intent.getParcelableExtra(MESSAGE);
                    if (messageModel != null) {
                        TAPRoomModel room = messageModel.getRoom();
                        TapUIChatActivity.start(
                                context,
                                instanceKey,
                                room.getRoomID(),
                                room.getName(),
                                room.getImageURL(),
                                room.getType(),
                                room.getColor(),
                                messageModel.getLocalID());
                    }
                    break;
                default:
                    // Update room list
                    String roomID = intent.getStringExtra(ROOM_ID);
                    switch (intent.getAction()) {
                        case RELOAD_ROOM_LIST:
                            if (activity != null && !activity.isFinishing() && intent.getBooleanExtra(IS_NEED_REFRESH, false)) {
                                TAPDataManager.getInstance(instanceKey).getRoomLastMessage(roomID, new TAPDatabaseListener<>() {
                                    @Override
                                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                                        super.onSelectFinished(entities);
                                        if (!entities.isEmpty()) {
                                            TAPMessageModel messageModel = TAPMessageModel.fromMessageEntity(entities.get(entities.size() - 1));
                                            vm.getRoomList().get(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID))).setLastMessage(messageModel);
                                            if (null != adapter) {
                                                activity.runOnUiThread(() -> {
                                                    adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID)));
                                                });
                                            }
                                        }
                                        else {
                                            activity.runOnUiThread(() -> {
                                                int index = vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID));
                                                vm.getRoomList().remove(index);
                                                vm.getRoomPointer().remove(roomID);
                                                adapter.notifyItemRemoved(index);
                                            });
                                        }
                                    }
                                });
                            }
                            if (null != adapter) {
                                adapter.notifyItemChanged(vm.getRoomList().indexOf(
                                        vm.getRoomPointer().get(roomID)));
                            }
                            break;
                        case CLEAR_ROOM_LIST_BADGE:
                            TAPRoomListModel room = vm.getRoomPointer().get(roomID);
                            if (null != room) {
                                room.setNumberOfUnreadMessages(0);
                                room.setNumberOfUnreadMentions(0);
                                TAPMessageStatusManager.getInstance(instanceKey).clearUnreadListPerRoomID(roomID);
                                TAPMessageStatusManager.getInstance(instanceKey).clearUnreadMentionPerRoomID(roomID);
                            }
                            break;
                    }
                    break;
            }
        }
    };

    private void calculateBadgeCount() {
        vm.setRoomBadgeCount(0);
        try {
            for (Map.Entry<String, TAPRoomListModel> entry : new HashMap<>(vm.getRoomPointer()).entrySet()) {
                if (entry.getValue().getNumberOfUnreadMessages() > 0) {
                    vm.setRoomBadgeCount(vm.getRoomBadgeCount() + entry.getValue().getNumberOfUnreadMessages());
                }
                else if (entry.getValue().isMarkedAsUnread()) {
                    vm.setRoomBadgeCount(vm.getRoomBadgeCount() + 1);
                }
            }
        }
        catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
        if (vm.getLastBadgeCount() != vm.getRoomBadgeCount()) {
            List<TapListener> listeners = getTapTalkListeners(instanceKey);
            if (listeners != null && !listeners.isEmpty()) {
                for (TapListener listener : listeners) {
                    listener.onTapTalkUnreadChatRoomBadgeCountUpdated(vm.getRoomBadgeCount());
                }
            }
            vm.setLastBadgeCount(vm.getRoomBadgeCount());
        }
    }

    private void addSocketListener() {
        if (TapTalk.getTapTalkSocketConnectionMode(instanceKey) == TapTalk.TapTalkSocketConnectionMode.CONNECT_IF_NEEDED) {
            socketListener = new TAPSocketListener() {
                @Override
                public void onSocketDisconnected() {
                    if (!TapTalk.isConnected(instanceKey) && TapTalk.isForeground) {
                        TapTalk.connect(instanceKey, new TapCommonListener() {
                            @Override
                            public void onSuccess(String successMessage) {
                                super.onSuccess(successMessage);
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {
                                super.onError(errorCode, errorMessage);
                            }
                        });
                    }
                }
            };
            TAPConnectionManager.getInstance(instanceKey).addSocketListener(socketListener);
        }
    }

    private void removeSocketListener() {
        TAPConnectionManager.getInstance(instanceKey).removeSocketListener(socketListener);
    }

    private void checkAndUpdateContactList() {
        // Sync contacts
        TapCoreContactManager.getInstance(instanceKey).fetchAllUserContactsFromServer(null);
    }
}
