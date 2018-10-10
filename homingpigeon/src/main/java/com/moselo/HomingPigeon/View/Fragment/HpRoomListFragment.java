package com.moselo.HomingPigeon.View.Fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moselo.HomingPigeon.API.View.HpDefaultDataView;
import com.moselo.HomingPigeon.BuildConfig;
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Listener.HpChatListener;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Interface.RoomListInterface;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpErrorModel;
import com.moselo.HomingPigeon.Model.HpMessageModel;
import com.moselo.HomingPigeon.Model.ResponseModel.HpGetRoomListResponse;
import com.moselo.HomingPigeon.Model.HpRoomListModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.HpNewChatActivity;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;
import com.moselo.HomingPigeon.View.Adapter.HpRoomListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpRoomListViewModel;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_MY_USERNAME;

public class HpRoomListFragment extends Fragment {

    private String TAG = HpRoomListFragment.class.getSimpleName();
    private Activity activity;

    private ConstraintLayout clButtonSearch, clSelection;
    private FrameLayout flSetupContainer;
    private LinearLayout llRoomEmpty;
    private TextView tvSelectionCount;
    private ImageView ivButtonCancelSelection, ivButtonMute, ivButtonDelete, ivButtonMore;
    private ProgressBar pbSettingUp;
    private FloatingActionButton fabNewChat;

    private RecyclerView rvContactList;
    private LinearLayoutManager llm;
    private HpRoomListAdapter adapter;
    private RoomListInterface roomListInterface;
    private HpRoomListViewModel vm;

    HpChatListener chatListener = new HpChatListener() {
        @Override
        public void onReceiveMessageInOtherRoom(HpMessageModel message) {
            processMessageFromSocket(message);
        }

        @Override
        public void onUpdateMessageInOtherRoom(HpMessageModel message) {
            processMessageFromSocket(message);
        }

        @Override
        public void onDeleteMessageInOtherRoom(HpMessageModel message) {
            processMessageFromSocket(message);
        }
    };

    public HpRoomListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.hp_fragment_room_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initListener();
        initView(view);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpRoomListViewModel.class);
    }

    private void initListener() {
        roomListInterface = (roomListModel, isSelected) -> {
            if (null != roomListModel && isSelected) {
                vm.getSelectedRooms().put(roomListModel.getLastMessage().getLocalID(), roomListModel);
            } else if (null != roomListModel) {
                vm.getSelectedRooms().remove(roomListModel.getLastMessage().getLocalID());
            }
            if (vm.getSelectedCount() > 0) {
                showSelectionActionBar();
            } else {
                hideSelectionActionBar();
            }
        };
    }

    private void initView(View view) {
        activity.getWindow().setBackgroundDrawable(null);

        clButtonSearch = view.findViewById(R.id.cl_button_search);
        clSelection = view.findViewById(R.id.cl_selection);
        flSetupContainer = view.findViewById(R.id.fl_setup_container);
        llRoomEmpty = view.findViewById(R.id.ll_room_empty);
        tvSelectionCount = view.findViewById(R.id.tv_selection_count);
        ivButtonCancelSelection = view.findViewById(R.id.iv_button_cancel_selection);
        ivButtonMute = view.findViewById(R.id.iv_button_mute);
        ivButtonDelete = view.findViewById(R.id.iv_button_delete);
        ivButtonMore = view.findViewById(R.id.iv_button_more);
        pbSettingUp = view.findViewById(R.id.pb_setting_up);
        fabNewChat = view.findViewById(R.id.fab_new_chat);
        rvContactList = view.findViewById(R.id.rv_contact_list);

        pbSettingUp.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.amethyst), PorterDuff.Mode.SRC_IN);

        if (vm.isSelecting()) showSelectionActionBar();

        adapter = new HpRoomListAdapter(vm, activity.getIntent().getStringExtra(K_MY_USERNAME), roomListInterface);
        llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(llm);
        rvContactList.setHasFixedSize(true);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        SimpleItemAnimator messageAnimator = (SimpleItemAnimator) rvContactList.getItemAnimator();
        if (null != messageAnimator) messageAnimator.setSupportsChangeAnimations(false);

        clButtonSearch.setOnClickListener(v -> ((HpRoomListActivity) activity).showSearchChat());

        fabNewChat.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HpNewChatActivity.class);
            startActivity(intent);
        });

        ivButtonCancelSelection.setOnClickListener(v -> {
            for (Map.Entry<String, HpRoomListModel> entry : vm.getSelectedRooms().entrySet()) {
                entry.getValue().getLastMessage().getRoom().setSelected(false);
            }
            vm.getSelectedRooms().clear();
            adapter.notifyDataSetChanged();
            hideSelectionActionBar();
        });

        ivButtonMute.setOnClickListener(v -> {

        });

        ivButtonDelete.setOnClickListener(v -> {

        });

        ivButtonMore.setOnClickListener(v -> {

        });

        flSetupContainer.setOnClickListener(v -> {
        });
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
        clButtonSearch.setElevation(HpUtils.getInstance().dpToPx(2));
        clButtonSearch.setVisibility(View.VISIBLE);
        clSelection.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        HpChatManager.getInstance().addChatListener(chatListener);
        viewAppearSequence();
    }

    @Override
    public void onPause() {
        super.onPause();
        HpChatManager.getInstance().removeChatListener(chatListener);
    }

    private void viewAppearSequence() {
        if (HpRoomListViewModel.isShouldNotLoadFromAPI()) {
            HpDataManager.getInstance().getRoomList(vm.getMyUserID(), true, dbListener);
        } else {
            runFullRefreshSequence();
        }
    }

    private void runFullRefreshSequence() {
        if (vm.getRoomList().size() > 0)
            HpDataManager.getInstance().getRoomList(vm.getMyUserID(), HpChatManager.getInstance().getSaveMessages(), true, dbListener);
        else
            HpDataManager.getInstance().getRoomList(vm.getMyUserID(), HpChatManager.getInstance().getSaveMessages(), false, dbListener);
    }

    private void fetchDataFromAPI() {
        if (vm.isDoneFirstSetup()) {
            // TODO: 08/10/18 nanti masukin api untuk call pending message
            HpDataManager.getInstance().getRoomListFromAPI(HpDataManager.getInstance().getActiveUser(getContext()).getUserID(), roomListView);
        } else {
            HpDataManager.getInstance().getRoomListFromAPI(HpDataManager.getInstance().getActiveUser(getContext()).getUserID(), roomListView);
        }
    }

    HpDefaultDataView<HpGetRoomListResponse> roomListView = new HpDefaultDataView<HpGetRoomListResponse>() {
        @Override
        public void startLoading() {
            super.startLoading();
        }

        @Override
        public void endLoading() {
            super.endLoading();
            fabNewChat.show();
        }

        @Override
        public void onSuccess(HpGetRoomListResponse response) {
            super.onSuccess(response);
            vm.setDoneFirstSetup(true);
            vm.setApiCalled(true);

            List<HpMessageEntity> tempMessage = new ArrayList<>();
            for (HpMessageModel message : response.getMessages()) {
                try {
                    HpMessageModel temp = HpMessageModel.BuilderDecrypt(message);
                    tempMessage.add(HpChatManager.getInstance().convertToEntity(temp));
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onSuccess: ", e);
                }
            }

            HpDataManager.getInstance().insertToDatabase(tempMessage, false, new HpDatabaseListener() {
                @Override
                public void onInsertFinished() {
                    viewAppearSequence();
                }
            });
        }

        @Override
        public void onError(HpErrorModel error) {
            super.onError(error);
            if (BuildConfig.DEBUG) Log.e(TAG, "onError: " + error.getMessage());
            flSetupContainer.setVisibility(View.GONE);
        }

        @Override
        public void onError(String errorMessage) {
            super.onError(errorMessage);
            if (BuildConfig.DEBUG) Log.e(TAG, "onError: " + errorMessage);
            flSetupContainer.setVisibility(View.GONE);
        }
    };

    HpDatabaseListener dbListener = new HpDatabaseListener() {
        @Override
        public void onSelectFinished(List<HpMessageEntity> entities) {
            List<HpRoomListModel> messageModels = new ArrayList<>();
            for (HpMessageEntity entity : entities) {
                HpMessageModel model = HpChatManager.getInstance().convertToModel(entity);
                HpRoomListModel roomModel = HpRoomListModel.buildWithLastMessage(model);
                messageModels.add(roomModel);
                vm.addRoomPointer(roomModel);
                HpDataManager.getInstance().getUnreadCountPerRoom(vm.getMyUserID(), entity.getRoomID(), dbListener);
            }

            vm.setRoomList(messageModels);

            activity.runOnUiThread(() -> {
                if (null != adapter && 0 == vm.getRoomList().size()) {
                    llRoomEmpty.setVisibility(View.VISIBLE);
                } else if (null != adapter && !HpRoomListViewModel.isShouldNotLoadFromAPI()) {
                    adapter.addRoomList(vm.getRoomList());
                    rvContactList.scrollToPosition(0);
                    llRoomEmpty.setVisibility(View.GONE);
                } else if (null != adapter && HpRoomListViewModel.isShouldNotLoadFromAPI()) {
                    adapter.setItems(vm.getRoomList(), false);
                    llRoomEmpty.setVisibility(View.GONE);
                }
                if (!HpRoomListViewModel.isShouldNotLoadFromAPI()) {
                    HpRoomListViewModel.setShouldNotLoadFromAPI(true);
                    fetchDataFromAPI();
                } else {
                    vm.setApiCalled(false);
                    flSetupContainer.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onCountedUnreadCount(String roomID, int unreadCount) {
            activity.runOnUiThread(() -> {
                vm.getRoomPointer().get(roomID).setUnreadCount(unreadCount);
                adapter.notifyItemChanged(vm.getRoomList().indexOf(vm.getRoomPointer().get(roomID)));
            });
        }

        @Override
        public void onSelectedRoomList(List<HpMessageEntity> entities, Map<String, Integer> unreadMap) {
            List<HpRoomListModel> messageModels = new ArrayList<>();
            for (HpMessageEntity entity : entities) {
                HpMessageModel model = HpChatManager.getInstance().convertToModel(entity);
                HpRoomListModel roomModel = HpRoomListModel.buildWithLastMessage(model);
                messageModels.add(roomModel);
                vm.addRoomPointer(roomModel);
                vm.getRoomPointer().get(entity.getRoomID()).setUnreadCount(unreadMap.get(entity.getRoomID()));
            }

            vm.setRoomList(messageModels);

            activity.runOnUiThread(() -> {
                if (null != adapter && 0 == vm.getRoomList().size()) {
                    llRoomEmpty.setVisibility(View.VISIBLE);
                } else if (null != adapter && vm.isApiCalled()) {
                    adapter.addRoomList(vm.getRoomList());
                    rvContactList.scrollToPosition(0);
                    llRoomEmpty.setVisibility(View.GONE);
                } else if (null != adapter && !vm.isApiCalled()) {
                    adapter.setItems(vm.getRoomList(), false);
                    llRoomEmpty.setVisibility(View.GONE);
                }
                if (!HpRoomListViewModel.isShouldNotLoadFromAPI()) {
                    HpRoomListViewModel.setShouldNotLoadFromAPI(true);
                    fetchDataFromAPI();
                } else {
                    vm.setApiCalled(false);
                    flSetupContainer.setVisibility(View.GONE);
                }
            });
        }
    };

    public void processMessageFromSocket (HpMessageModel message) {
        String  messageRoomID = message.getRoom().getRoomID();

        HpRoomListModel roomList = vm.getRoomPointer().get(messageRoomID);

        if (null != roomList) {
            //room nya ada di listnya
            Log.e(TAG, "processMessageFromSocket: "+messageRoomID+" : "+HpUtils.getInstance().toJsonString(roomList) );

            HpMessageModel roomLastMessage = roomList.getLastMessage();

            if (roomLastMessage.getLocalID().equals(message.getLocalID())) {
                //last messagenya sama cuma update datanya aja
                roomLastMessage.setUpdated(message.getUpdated());
                roomLastMessage.setDeleted(message.getDeleted());
                roomLastMessage.setSending(message.getSending());
                roomLastMessage.setFailedSend(message.getFailedSend());
                roomLastMessage.setHasRead(message.getHasRead());
                roomLastMessage.setIsRead(message.getIsRead());
                roomLastMessage.setDelivered(message.getDelivered());
                roomLastMessage.setHidden(message.getHidden());

                Integer roomPos = vm.getRoomList().indexOf(roomList);
                activity.runOnUiThread(() -> adapter.notifyItemChanged(roomPos));
            } else {
                //last message nya beda sama yang ada di tampilan
                roomList.setLastMessage(message);

                if (!roomList.getLastMessage().getUser().getUserID()
                        .equals(HpDataManager.getInstance().getActiveUser(getContext()).getUserID())) {
                    //kalau beda yang ngirim unread countnya tambah 1
                    roomList.setUnreadCount(roomList.getUnreadCount() + 1);
                }

                int oldPos = vm.getRoomList().indexOf(roomList);
                vm.getRoomList().remove(roomList);
                vm.getRoomList().add(0, roomList);
                activity.runOnUiThread(() -> {
                    boolean scrollToTop = llm.findFirstCompletelyVisibleItemPosition() == 0;
                    adapter.notifyItemChanged(oldPos);
                    adapter.notifyItemMoved(oldPos, 0);
                    if (scrollToTop) rvContactList.scrollToPosition(0);
                });
            }
        } else {
            //kalau room yang masuk baru
            HpRoomListModel newRoomList = new HpRoomListModel(message, 1);
            vm.addRoomPointer(newRoomList);
            vm.getRoomList().add(0, newRoomList);
            activity.runOnUiThread(() -> adapter.notifyItemInserted(0));
        }
    }
}
