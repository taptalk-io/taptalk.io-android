package io.taptalk.TapTalk.View.Fragment;

import static io.taptalk.TapTalk.ViewModel.TapMainRoomListViewModel.RoomListState.STATE_ROOM_LIST;
import static io.taptalk.TapTalk.ViewModel.TapMainRoomListViewModel.RoomListState.STATE_SEARCH_CHAT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.ViewModel.TapMainRoomListViewModel;

public class TapUIMainRoomListFragment extends Fragment {

    private static final String TAG = TapUIMainRoomListFragment.class.getSimpleName();

    private String instanceKey = "";

    public TapUIRoomListFragment fRoomList;
    public TapUISearchChatFragment fSearchFragment;
    private TapMainRoomListViewModel vm;
    private TapMainRoomListViewModel.RoomListState state = STATE_ROOM_LIST;

    public TapUIMainRoomListFragment() {
        // Required empty public constructor
    }

    private TapUIMainRoomListFragment(String instanceKey, @Nullable TapMainRoomListViewModel vm) {
        this.instanceKey = instanceKey;
        this.vm = vm;
    }

    public static TapUIMainRoomListFragment newInstance(String instanceKey) {
        return new TapUIMainRoomListFragment(instanceKey, null);
    }

    public static TapUIMainRoomListFragment newInstance(String instanceKey, @Nullable TapMainRoomListViewModel vm) {
        return new TapUIMainRoomListFragment(instanceKey, vm);
    }

    public String getInstanceKey() {
        return instanceKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (vm != null) {
            this.state = vm.getState();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tap_fragment_main_room_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != instanceKey) {
            initView();
        }
    }

    private void initView() {
//        fRoomList = (TapUIRoomListFragment) getChildFragmentManager().findFragmentById(R.id.fragment_room_list);
//        fSearchFragment = (TapUISearchChatFragment) getChildFragmentManager().findFragmentById(R.id.fragment_search_chat);

        if (fRoomList == null) {
            fRoomList = TapUIRoomListFragment.newInstance(instanceKey);
        }
        if (fSearchFragment == null) {
            fSearchFragment = TapUISearchChatFragment.newInstance(instanceKey, vm);
        }
        getChildFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_room_list, fRoomList)
            .replace(R.id.fragment_search_chat, fSearchFragment)
            .commit();

        if (vm != null && vm.getState() == STATE_SEARCH_CHAT) {
            showSearchChat();
        }
        else {
            showRoomList();
        }
    }

    public void showRoomList() {
        if (vm != null) {
            vm.setState(STATE_ROOM_LIST);
        }
        state = STATE_ROOM_LIST;
        getChildFragmentManager()
                .beginTransaction()
                .show(fRoomList)
                .hide(fSearchFragment)
                .commit();
    }

    public void showSearchChat() {
        if (vm != null) {
            vm.setState(STATE_SEARCH_CHAT);
        }
        state = STATE_SEARCH_CHAT;
        getChildFragmentManager()
                .beginTransaction()
                .show(fSearchFragment)
                .hide(fRoomList)
                .commit();
    }

    public void onBackPressed() {
        switch (state) {
            case STATE_ROOM_LIST:
                if (null != fRoomList && fRoomList.isSelecting()) {
                    fRoomList.cancelSelection();
                }
                else if (null != getActivity()) {
//                    getActivity().finish();
                    TAPChatManager.getInstance(instanceKey).triggerCloseRoomListButtonTapped(getActivity());
                }
                break;
            case STATE_SEARCH_CHAT:
                showRoomList();
                break;
        }
    }
}
