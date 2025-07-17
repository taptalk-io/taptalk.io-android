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
import androidx.lifecycle.ViewModelProvider;

import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.ViewModel.TAPVideoPlayerViewModel;
import io.taptalk.TapTalk.ViewModel.TapMainRoomListViewModel;

public class TapUIMainRoomListFragment extends Fragment {

    private static final String TAG = TapUIMainRoomListFragment.class.getSimpleName();

    private String instanceKey = "";

    private TapUIRoomListFragment fRoomList;
    private TapUISearchChatFragment fSearchFragment;
    private TapMainRoomListViewModel vm;

    public TapUIMainRoomListFragment() {
        // Required empty public constructor
    }

    private TapUIMainRoomListFragment(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TapUIMainRoomListFragment newInstance(String instanceKey) {
        return new TapUIMainRoomListFragment(instanceKey);
    }

    public String getInstanceKey() {
        return instanceKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(this).get(TapMainRoomListViewModel.class);
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

        fRoomList = TapUIRoomListFragment.newInstance(instanceKey);
        fSearchFragment = TapUISearchChatFragment.newInstance(instanceKey);
        getChildFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_room_list, fRoomList)
            .replace(R.id.fragment_search_chat, fSearchFragment)
            .commit();

        if (vm.getState() == STATE_SEARCH_CHAT) {
            showSearchChat();
        }
        else {
            showRoomList();
        }
    }

    public void showRoomList() {
        vm.setState(STATE_ROOM_LIST);
        getChildFragmentManager()
                .beginTransaction()
                .show(fRoomList)
                .hide(fSearchFragment)
                .commit();
    }

    public void showSearchChat() {
        vm.setState(STATE_SEARCH_CHAT);
        getChildFragmentManager()
                .beginTransaction()
                .show(fSearchFragment)
                .hide(fRoomList)
                .commit();
    }

    public void onBackPressed() {
        switch (vm.getState()) {
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
