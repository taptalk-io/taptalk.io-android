package com.moselo.HomingPigeon.View.Fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Helper.HpUtils;
import com.moselo.HomingPigeon.Helper.OverScrolled.OverScrollDecoratorHelper;
import com.moselo.HomingPigeon.Listener.HomingPigeonDatabaseListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Listener.RoomListListener;
import com.moselo.HomingPigeon.Manager.HpChatManager;
import com.moselo.HomingPigeon.Manager.HpConnectionManager;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Manager.HpNetworkStateManager;
import com.moselo.HomingPigeon.Model.ErrorModel;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.ResponseModel.GetRoomListResponse;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Activity.HpNewChatActivity;
import com.moselo.HomingPigeon.View.Activity.HpRoomListActivity;
import com.moselo.HomingPigeon.View.Adapter.HpRoomListAdapter;
import com.moselo.HomingPigeon.ViewModel.HpRoomListViewModel;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.K_MY_USERNAME;

public class HpRoomListFragment extends Fragment {

    private String TAG = HpRoomListFragment.class.getSimpleName();
    private Activity activity;

    private ConstraintLayout clButtonSearch, clSelection;
    private FrameLayout flSetupContainer;
    private LinearLayout llConnectionStatus, llRoomEmpty;
    private TextView tvSelectionCount, tvConnectionStatus;
    private ImageView ivButtonCancelSelection, ivButtonMute, ivButtonDelete, ivButtonMore, ivConnectionStatus;
    private ProgressBar pbConnecting, pbSettingUp;
    private FloatingActionButton fabNewChat;

    private RecyclerView rvContactList;
    private HpRoomListAdapter adapter;
    private RoomListListener roomListListener;
    private HpRoomListViewModel vm;

    private boolean isApiNeedToBeCalled = true;

    public HpRoomListFragment() {
    }

    public static HpRoomListFragment newInstance() {
        Bundle args = new Bundle();
        HpRoomListFragment fragment = new HpRoomListFragment();
        fragment.setArguments(args);
        return fragment;
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
        initConnectionStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HpConnectionManager.getInstance().removeSocketListener(socketListener);
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(HpRoomListViewModel.class);
    }

    private void initListener() {
        roomListListener = (messageModel, isSelected) -> {
            if (null != messageModel && isSelected) {
                vm.getSelectedRooms().put(messageModel.getLocalID(), messageModel);
            } else if (null != messageModel) {
                vm.getSelectedRooms().remove(messageModel.getLocalID());
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
        llConnectionStatus = view.findViewById(R.id.ll_connection_status);
        llRoomEmpty = view.findViewById(R.id.ll_room_empty);
        tvSelectionCount = view.findViewById(R.id.tv_selection_count);
        tvConnectionStatus = view.findViewById(R.id.tv_connection_status);
        ivButtonCancelSelection = view.findViewById(R.id.iv_button_cancel_selection);
        ivButtonMute = view.findViewById(R.id.iv_button_mute);
        ivButtonDelete = view.findViewById(R.id.iv_button_delete);
        ivButtonMore = view.findViewById(R.id.iv_button_more);
        ivConnectionStatus = view.findViewById(R.id.iv_connection_status);
        pbConnecting = view.findViewById(R.id.pb_connecting);
        pbSettingUp = view.findViewById(R.id.pb_setting_up);
        fabNewChat = view.findViewById(R.id.fab_new_chat);
        rvContactList = view.findViewById(R.id.rv_contact_list);

        pbConnecting.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        pbSettingUp.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.amethyst), PorterDuff.Mode.SRC_IN);

        if (vm.isSelecting()) showSelectionActionBar();

        adapter = new HpRoomListAdapter(vm, activity.getIntent().getStringExtra(K_MY_USERNAME), roomListListener);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(true);
        OverScrollDecoratorHelper.setUpOverScroll(rvContactList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        clButtonSearch.setOnClickListener(v -> ((HpRoomListActivity) activity).showSearchChat());

        fabNewChat.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HpNewChatActivity.class);
            startActivity(intent);
        });

        ivButtonCancelSelection.setOnClickListener(v -> {
            for (Map.Entry<String, MessageModel> entry : vm.getSelectedRooms().entrySet()) {
                entry.getValue().getRoom().setSelected(false);
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

    private void initConnectionStatus() {
        HpConnectionManager.getInstance().addSocketListener(socketListener);
        if (!HpNetworkStateManager.getInstance().hasNetworkConnection(getContext()))
            socketListener.onSocketDisconnected();
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

    private void setStatusConnected() {
        activity.runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.hp_bg_status_connected);
            tvConnectionStatus.setText(getString(R.string.connected));
            ivConnectionStatus.setImageResource(R.drawable.hp_ic_connected_white);
            ivConnectionStatus.setVisibility(View.VISIBLE);
            pbConnecting.setVisibility(View.GONE);
            llConnectionStatus.setVisibility(View.VISIBLE);

            new Handler().postDelayed(() -> llConnectionStatus.setVisibility(View.GONE), 500L);
        });
    }

    private void setStatusConnecting() {
        if (!HpNetworkStateManager.getInstance().hasNetworkConnection(getContext())) return;

        activity.runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.hp_bg_status_connecting);
            tvConnectionStatus.setText(R.string.connecting);
            ivConnectionStatus.setVisibility(View.GONE);
            pbConnecting.setVisibility(View.VISIBLE);
            llConnectionStatus.setVisibility(View.VISIBLE);
        });
    }

    private void setStatusWaitingForNetwork() {
        if (HpNetworkStateManager.getInstance().hasNetworkConnection(getContext())) return;

        activity.runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.hp_bg_status_offline);
            tvConnectionStatus.setText(R.string.waiting_for_network);
            ivConnectionStatus.setVisibility(View.GONE);
            pbConnecting.setVisibility(View.VISIBLE);
            llConnectionStatus.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        vm.clearRoomList();
        getRoomListFlow();
    }

    @Override
    public void onPause() {
        super.onPause();
        isApiNeedToBeCalled = true;
    }

    // Update connection status UI
    private HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {
        @Override
        public void onReceiveNewEmit(String eventName, String emitData) {

        }

        @Override
        public void onSocketConnected() {
            setStatusConnected();
        }

        @Override
        public void onSocketDisconnected() {
            setStatusWaitingForNetwork();
        }

        @Override
        public void onSocketConnecting() {
            setStatusConnecting();
        }

        @Override
        public void onSocketError() {
            setStatusWaitingForNetwork();
        }
    };

    private void getRoomListFlow() {
        HpDataManager.getInstance().getRoomList(HpChatManager.getInstance().getSaveMessages(), dbListener);
    }

    HpDefaultDataView<GetRoomListResponse> roomListView = new HpDefaultDataView<GetRoomListResponse>() {
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
        public void onSuccess(GetRoomListResponse response) {
            super.onSuccess(response);
            List<HpMessageEntity> tempMessage = new ArrayList<>();
            for (MessageModel message : response.getMessages()) {
                Log.e(TAG, message.getMessage() + " : " + message.getRoom().getRoomID());
                try {
                    MessageModel temp = MessageModel.BuilderDecrypt(message);
                    tempMessage.add(HpChatManager.getInstance().convertToEntity(temp));
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onSuccess: ", e);
                }
            }

            HpDataManager.getInstance().insertToDatabase(tempMessage, false, new HpDatabaseListener() {
                @Override
                public void onInsertFinished() {
                    isApiNeedToBeCalled = false;
                    HpDataManager.getInstance().getRoomList(dbListener);
                }
            });
        }

        @Override
        public void onError(ErrorModel error) {
            super.onError(error);
        }
    };

    HpDatabaseListener dbListener = new HpDatabaseListener() {
        @Override
        public void onSelectFinished(List<HpMessageEntity> entities) {
            List<MessageModel> messageModels = new ArrayList<>();
            for (HpMessageEntity entity : entities) {
                MessageModel model = HpChatManager.getInstance().convertToModel(entity);
                messageModels.add(model);
                vm.addRoomPointer(model);
            }

            vm.setRoomList(messageModels);

            getActivity().runOnUiThread(() -> {
                if (null != adapter && 0 == vm.getRoomList().size()) {
                    llRoomEmpty.setVisibility(View.VISIBLE);
                } else if (null != adapter) {
                    adapter.setItems(vm.getRoomList(), false);
                    llRoomEmpty.setVisibility(View.GONE);
                }

                for (Map.Entry<String, MessageModel> unread : vm.getRoomPointer().entrySet()) {
                    HpDataManager.getInstance().getUnreadCountPerRoom(unread.getKey(), dbListener);
                }

                if (isApiNeedToBeCalled)
                    HpDataManager.getInstance().getRoomListFromAPI(HpDataManager.getInstance().getActiveUser(getContext()).getUserID(), roomListView);
                else flSetupContainer.setVisibility(View.GONE);
            });
        }

        @Override
        public void onSelectUnread(String roomID, int unreadCount) {
            Log.e(TAG, "onSelectUnread: "+roomID +" : "+ unreadCount );
            vm.getRoomPointer().get(roomID).getRoom().setUnreadCount(unreadCount);
        }
    };
}
