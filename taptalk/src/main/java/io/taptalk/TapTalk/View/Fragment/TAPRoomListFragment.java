package io.taptalk.TapTalk.View.Fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Interface.TapTalkRoomListInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.TAPContactModel;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomListModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Activity.TAPNewChatActivity;
import io.taptalk.TapTalk.View.Adapter.TAPRoomListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;
import io.taptalk.Taptalk.R;

public class TAPRoomListFragment extends Fragment {

    private String TAG = TAPRoomListFragment.class.getSimpleName();
    private TAPMainRoomListFragment fragment;

    private ConstraintLayout clButtonSearch, clSelection;
    private FrameLayout flSetupContainer;
    private LinearLayout llRoomEmpty;
    private TextView tvSelectionCount;
    private ImageView ivButtonNewChat, ivButtonCancelSelection, ivButtonMute, ivButtonDelete, ivButtonMore;

    private RecyclerView rvContactList;
    private LinearLayoutManager llm;
    private TAPRoomListAdapter adapter;
    private TapTalkRoomListInterface tapTalkRoomListInterface;
    private TAPRoomListViewModel vm;

    private TAPChatListener chatListener;

    private TapTalkNetworkInterface networkListener = () -> {
        if (vm.isDoneFirstSetup()) {
            updateQueryRoomListFromBackground();
        }
    };

    public TAPRoomListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment = (TAPMainRoomListFragment) this.getParentFragment();
        return inflater.inflate(R.layout.tap_fragment_room_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initListener();
        initView(view);
        viewLoadedSequence();
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: 29 October 2018 UPDATE UNREAD BADGE
        TAPNotificationManager.getInstance().setRoomListAppear(true);
        TAPChatManager.getInstance().saveMessageToDatabase();
        updateQueryRoomListFromBackground();
        addNetworkListener();
        new Thread(() -> TAPDataManager.getInstance().getMyContactListFromAPI(getContactView)).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        TAPNotificationManager.getInstance().setRoomListAppear(false);
        removeNetworkListener();
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
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPRoomListViewModel.class);
    }

    private void initListener() {
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
            public void onSendTextMessage(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onReadMessage(String roomID) {
                updateUnreadCountPerRoom(roomID);
            }

            @Override
            public void onReceiveStartTyping(TAPTypingModel typingModel) {
                showTyping(typingModel, true);
            }

            @Override
            public void onReceiveStopTyping(TAPTypingModel typingModel) {
                showTyping(typingModel, false);
            }
        };
        TAPChatManager.getInstance().addChatListener(chatListener);

        tapTalkRoomListInterface = () -> {
            if (vm.getSelectedCount() > 0) {
                showSelectionActionBar();
            } else {
                hideSelectionActionBar();
            }
        };
    }

    private void initView(View view) {
        clButtonSearch = view.findViewById(R.id.cl_button_search);
        clSelection = view.findViewById(R.id.cl_selection);
        flSetupContainer = view.findViewById(R.id.fl_setup_container);
        llRoomEmpty = view.findViewById(R.id.ll_room_empty);
        tvSelectionCount = view.findViewById(R.id.tv_selection_count);
        ivButtonNewChat = view.findViewById(R.id.iv_button_new_chat);
        ivButtonCancelSelection = view.findViewById(R.id.iv_button_cancel_selection);
        ivButtonMute = view.findViewById(R.id.iv_button_mute);
        ivButtonDelete = view.findViewById(R.id.iv_button_delete);
        ivButtonMore = view.findViewById(R.id.iv_button_more);
        rvContactList = view.findViewById(R.id.rv_contact_list);

        if (null != getActivity()) {
            getActivity().getWindow().setBackgroundDrawable(null);
        }

        flSetupContainer.setVisibility(View.GONE);

        if (vm.isSelecting()) {
            showSelectionActionBar();
        }
        vm.setDoneFirstApiSetup(TAPDataManager.getInstance().isRoomListSetupFinished());

        adapter = new TAPRoomListAdapter(vm, tapTalkRoomListInterface);
        llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(llm);
        rvContactList.setHasFixedSize(true);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        SimpleItemAnimator messageAnimator = (SimpleItemAnimator) rvContactList.getItemAnimator();
        if (null != messageAnimator) messageAnimator.setSupportsChangeAnimations(false);

        clButtonSearch.setOnClickListener(v -> {
            if (null != fragment)
                fragment.showSearchChat();
        });
        ivButtonNewChat.setOnClickListener(v -> openNewChatActivity());
        ivButtonCancelSelection.setOnClickListener(v -> cancelSelection());
        ivButtonMute.setOnClickListener(v -> {

        });
        ivButtonDelete.setOnClickListener(v -> {

        });
        ivButtonMore.setOnClickListener(v -> {

        });
        flSetupContainer.setOnClickListener(v -> {
        });
    }

    private void openNewChatActivity() {
        Intent intent = new Intent(getContext(), TAPNewChatActivity.class);
        startActivity(intent);
        if (null != getActivity()) {
            getActivity().overridePendingTransition(R.anim.tap_slide_up, R.anim.tap_stay);
        }
    }

    //ini adalah fungsi yang di panggil pertama kali pas onResume
    private void viewLoadedSequence() {
        if (TAPRoomListViewModel.isShouldNotLoadFromAPI()) {
            //selama di apps (foreground) ga perlu panggil API, ambil local dari database aja
            TAPDataManager.getInstance().getRoomList(true, dbListener);
        } else {
            //kalau kita dari background atau pertama kali buka apps, kita baru jalanin full cycle
            runFullRefreshSequence();
        }
    }

    private void getDatabaseAndAnimateResult() {
        //viewnya pake yang dbAnimatedListener
        TAPDataManager.getInstance().getRoomList(true, dbAnimatedListener);
    }

    //ini fungsi untuk manggil full cycle dari room List
    private void runFullRefreshSequence() {
        if (vm.getRoomList().size() > 0) {
            //kalau ga recyclerView ga kosong, kita check dan update unread dlu baru update tampilan
            TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), true, dbListener);
        } else {
            //kalau recyclerView masih kosong, kita tampilin room dlu baru update unreadnya
            TAPDataManager.getInstance().getRoomList(TAPChatManager.getInstance().getSaveMessages(), false, dbListener);
        }
    }

    private void fetchDataFromAPI() {
        //kalau pertama kali kita call api getMessageRoomListAndUnread
        //setelah itu kita panggilnya api pending and update message
        if (vm.isDoneFirstApiSetup()) {
            TAPDataManager.getInstance().getNewAndUpdatedMessage(roomListView);
        } else {
            TAPDataManager.getInstance().getMessageRoomListAndUnread(TAPDataManager.getInstance().getActiveUser().getUserID(), roomListView);
        }
    }

    /*ini adalah fungsi yang update tampilan setelah dapet data dari database
     * parameter isAnimated itu gunanya buat nentuin datanya kalau berubah perlu di animate atau nggak*/
    private void reloadLocalDataAndUpdateUILogic(boolean isAnimated) {
        if (null != getActivity()) {
            getActivity().runOnUiThread(() -> {
                if (null != adapter && 0 == vm.getRoomList().size()) {
                    llRoomEmpty.setVisibility(View.VISIBLE);
                } else if (null != adapter && (!TAPRoomListViewModel.isShouldNotLoadFromAPI() || isAnimated) && TAPNotificationManager.getInstance().isRoomListAppear()) {
                    //ini ngecek isShouldNotLoadFromAPI, kalau false brati dy pertama kali buka apps atau dari background
                    //isShouldNotLoadFromAPI ini buat load dari database yang pertama
                    //is animate nya itu buat kalau misalnya perlu di animate dari parameter
                    adapter.addRoomList(vm.getRoomList());
                    rvContactList.scrollToPosition(0);
                    llRoomEmpty.setVisibility(View.GONE);
                } else if (null != adapter && TAPRoomListViewModel.isShouldNotLoadFromAPI()) {
                    //ini pas kalau ga perlu untuk di animate changesnya
                    adapter.setItems(vm.getRoomList(), false);
                    llRoomEmpty.setVisibility(View.GONE);
                }
                flSetupContainer.setVisibility(View.GONE);
                showNewChatButton();

                //ini buat ngubah isShouldNotLoadFromAPInya pas get data pertama
                //setelah itu fetch data dari api sesuai dengan cycle yang di butuhin
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

        if (null != roomList) {
            //room nya ada di listnya
            TAPMessageModel roomLastMessage = roomList.getLastMessage();

            if (roomLastMessage.getLocalID().equals(message.getLocalID()) && null != getActivity()) {
                //last messagenya sama cuma update datanya aja
                roomLastMessage.updateValue(message);
                Integer roomPos = vm.getRoomList().indexOf(roomList);
                getActivity().runOnUiThread(() -> adapter.notifyItemChanged(roomPos));
            } else {
                //last message nya beda sama yang ada di tampilan
                roomList.setLastMessage(message);

                // Add unread count by 1 if sender is not self
                if (!roomList.getLastMessage().getUser().getUserID()
                        .equals(TAPDataManager.getInstance().getActiveUser().getUserID())) {
                    roomList.setUnreadCount(roomList.getUnreadCount() + 1);
                }

                // Move room to top
                int oldPos = vm.getRoomList().indexOf(roomList);
                vm.getRoomList().remove(roomList);
                vm.getRoomList().add(0, roomList);

                getActivity().runOnUiThread(() -> {
                    llRoomEmpty.setVisibility(View.GONE);
                    adapter.notifyItemChanged(oldPos);
                    adapter.notifyItemMoved(oldPos, 0);
                    // Scroll to top
                    if (llm.findFirstCompletelyVisibleItemPosition() == 0)
                        rvContactList.scrollToPosition(0);
                });
            }
        } else if (null != getActivity()) {
            //kalau room yang masuk baru

            //TAPRoomListModel newRoomList = new TAPRoomListModel(message, 1);
            TAPRoomListModel newRoomList = TAPRoomListModel.buildWithLastMessage(message);
            if (!newRoomList.getLastMessage().getUser().getUserID()
                    .equals(TAPDataManager.getInstance().getActiveUser().getUserID())) {
                newRoomList.setUnreadCount(1);
            }

            vm.addRoomPointer(newRoomList);
            vm.getRoomList().add(0, newRoomList);
            getActivity().runOnUiThread(() -> {
                llRoomEmpty.setVisibility(View.GONE);
                if (0 == adapter.getItems().size())
                    adapter.addItem(newRoomList);
                adapter.notifyItemInserted(0);
            });
        }
    }

    private void showSelectionActionBar() {
        vm.setSelecting(true);
        tvSelectionCount.setText(vm.getSelectedCount() + "");
        clButtonSearch.setElevation(0);
        clButtonSearch.setVisibility(View.INVISIBLE);
        clSelection.setVisibility(View.VISIBLE);
    }

    private void hideSelectionActionBar() {
        vm.setSelecting(false);
        clButtonSearch.setElevation(TAPUtils.getInstance().dpToPx(2));
        clButtonSearch.setVisibility(View.VISIBLE);
        clSelection.setVisibility(View.INVISIBLE);
    }

    public void cancelSelection() {
        vm.getSelectedRooms().clear();
        adapter.notifyDataSetChanged();
        hideSelectionActionBar();
    }

    private void showNewChatButton() {
        if (ivButtonNewChat.getVisibility() == View.VISIBLE) {
            return;
        }
        ivButtonNewChat.setTranslationY(TAPUtils.getInstance().dpToPx(120));
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
                .translationY(TAPUtils.getInstance().dpToPx(120))
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> ivButtonNewChat.setVisibility(View.GONE))
                .start();
    }

    public boolean isSelecting() {
        return vm.isSelecting();
    }

    private void showTyping(TAPTypingModel typingModel, boolean isTyping) {
        String roomID = typingModel.getRoomID();
        if (!vm.getRoomPointer().containsKey(roomID)) {
            return;
        }
        TAPRoomListModel roomListModel = vm.getRoomPointer().get(roomID);
        roomListModel.setTyping(isTyping);
        getActivity().runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(roomListModel)));
    }

    private TapDefaultDataView<TAPGetRoomListResponse> roomListView = new TapDefaultDataView<TAPGetRoomListResponse>() {
        @Override
        public void startLoading() {
            //ini buat munculin setup dialog pas pertama kali buka apps
            if (!vm.isDoneFirstApiSetup()) {
                flSetupContainer.setVisibility(View.VISIBLE);
                hideNewChatButton();
            }
        }

        @Override
        public void endLoading() {
            //save preference kalau kita udah munculin setup dialog
            if (!vm.isDoneFirstApiSetup()) {
                vm.setDoneFirstApiSetup(true);
                TAPDataManager.getInstance().setRoomListSetupFinished();
            }
        }

        @Override
        public void onSuccess(TAPGetRoomListResponse response) {
            super.onSuccess(response);
            //sebagai tanda kalau udah manggil api (Get message from API)
            vm.setDoneFirstSetup(true);

            if (response.getMessages().size() > 0) {
                List<TAPMessageEntity> tempMessage = new ArrayList<>();
                List<TAPMessageModel> deliveredMessages = new ArrayList<>();
                for (HashMap<String, Object> messageMap : response.getMessages()) {
                    try {
                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                        tempMessage.add(TAPChatManager.getInstance().convertToEntity(message));

                        // Save undelivered messages to list
                        if (null == message.getDelivered() || (null != message.getDelivered() && !message.getDelivered())) {
                            deliveredMessages.add(message);
                        }

                        // Save user data to contact manager
                        // TODO: 25 January 2019 CALL API IF USER IS SELF
                        TAPContactManager.getInstance().updateUserDataMap(message.getUser());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Update status to delivered
                if (deliveredMessages.size() > 0) {
                    TAPMessageStatusManager.getInstance().updateMessageStatusToDeliveredFromNotification(deliveredMessages);
                }

                //hasil dari API disimpen ke dalem database
                TAPDataManager.getInstance().insertToDatabase(tempMessage, false, new TAPDatabaseListener() {
                    @Override
                    public void onInsertFinished() {
                        //setelah selesai insert database, kita panggil fungsi buat ambil data terbaru dari
                        //database dan animasiin perubahannya
                        getDatabaseAndAnimateResult();
                    }
                });
            } else {
                reloadLocalDataAndUpdateUILogic(true);
            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            super.onError(error);
            Log.e(TAG, "onError: " + error.getMessage());
            flSetupContainer.setVisibility(View.GONE);
            showNewChatButton();
        }

        @Override
        public void onError(String errorMessage) {
            super.onError(errorMessage);
            Log.e(TAG, "onError: " + errorMessage);
            flSetupContainer.setVisibility(View.GONE);
            showNewChatButton();
        }
    };

    private TapDefaultDataView<TAPContactResponse> getContactView = new TapDefaultDataView<TAPContactResponse>() {
        @Override
        public void onSuccess(TAPContactResponse response) {
            try {
                // Insert contacts to database
                if (null == response.getContacts() || response.getContacts().isEmpty()) {
                    return;
                }
                List<TAPUserModel> users = new ArrayList<>();
                for (TAPContactModel contact : response.getContacts()) {
                    users.add(contact.getUser().setUserAsContact());
                }
                TAPDataManager.getInstance().insertMyContactToDatabase(users);
                TAPContactManager.getInstance().updateUserDataMap(users);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> dbListener = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            List<TAPRoomListModel> messageModels = new ArrayList<>();
            //ngubah entity yang dari database jadi model
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPChatManager.getInstance().convertToModel(entity);
                TAPRoomListModel roomModel = TAPRoomListModel.buildWithLastMessage(model);
                messageModels.add(roomModel);
                vm.addRoomPointer(roomModel);
                //update unread count nya per room
                TAPDataManager.getInstance().getUnreadCountPerRoom(entity.getRoomID(), dbListener);
            }

            vm.setRoomList(messageModels);
            //update UI
            reloadLocalDataAndUpdateUILogic(false);
        }

        @Override
        public void onCountedUnreadCount(String roomID, int unreadCount) {
            if (null != getActivity() && vm.getRoomPointer().containsKey(roomID)) {
                vm.getRoomPointer().get(roomID).setUnreadCount(unreadCount);
                getActivity().runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID))));
            }
        }

        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap) {
            List<TAPRoomListModel> messageModels = new ArrayList<>();
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPChatManager.getInstance().convertToModel(entity);
                TAPRoomListModel roomModel = TAPRoomListModel.buildWithLastMessage(model);
                messageModels.add(roomModel);
                vm.addRoomPointer(roomModel);
                vm.getRoomPointer().get(entity.getRoomID()).setUnreadCount(unreadMap.get(entity.getRoomID()));
            }

            vm.setRoomList(messageModels);
            reloadLocalDataAndUpdateUILogic(false);
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
                vm.getRoomPointer().get(entity.getRoomID()).setUnreadCount(unreadMap.get(entity.getRoomID()));
            }

            vm.setRoomList(messageModels);
            reloadLocalDataAndUpdateUILogic(true);
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
            if (null != getActivity() && vm.getRoomPointer().containsKey(roomID) && TAPMessageStatusManager.getInstance().getUnreadList().containsKey(roomID)) {
                vm.getRoomPointer().get(roomID).setUnreadCount(vm.getRoomPointer().get(roomID).getUnreadCount() - TAPMessageStatusManager.getInstance().getUnreadList().get(roomID));
                TAPMessageStatusManager.getInstance().clearUnreadListPerRoomID(roomID);
                getActivity().runOnUiThread(() -> adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID))));
            }
        }).start();
    }
}
