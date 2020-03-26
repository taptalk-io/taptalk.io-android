package io.taptalk.TapTalk.View.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.taptalk.TapTalk.R;

public class TapUIMainRoomListFragment extends Fragment {

    private static final String TAG = TapUIMainRoomListFragment.class.getSimpleName();

    private String instanceKey = "";

    private enum RoomListState {
        STATE_SEARCH_CHAT, STATE_ROOM_LIST
    }

    private TapUIRoomListFragment fRoomList;
    private TapUISearchChatFragment fSearchFragment;
    private RoomListState state = RoomListState.STATE_ROOM_LIST;

    public TapUIMainRoomListFragment() {
        // Required empty public constructor
    }

    private TapUIMainRoomListFragment(String instanceKey) {
        setInstanceKey(instanceKey);
    }

    public static TapUIMainRoomListFragment newInstance(String instanceKey) {
        return new TapUIMainRoomListFragment(instanceKey);
    }

    public String getInstanceKey() {
        return instanceKey;
    }

    public void setInstanceKey(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_room_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        fRoomList = (TapUIRoomListFragment) getChildFragmentManager().findFragmentById(R.id.fragment_room_list);
        fSearchFragment = (TapUISearchChatFragment) getChildFragmentManager().findFragmentById(R.id.fragment_search_chat);

        showRoomList();
    }

    public void showRoomList() {
        state = RoomListState.STATE_ROOM_LIST;
        getChildFragmentManager()
                .beginTransaction()
                .show(fRoomList)
                .hide(fSearchFragment)
                .commit();
    }

    public void showSearchChat() {
        state = RoomListState.STATE_SEARCH_CHAT;
        getChildFragmentManager()
                .beginTransaction()
                .show(fSearchFragment)
                .hide(fRoomList)
                .commit();
    }

    public void onBackPressed() {
        switch (state) {
            case STATE_ROOM_LIST:
                if (fRoomList.isSelecting()) {
                    fRoomList.cancelSelection();
                } else if (null != getActivity()) {
                    getActivity().finish();
                }
                break;
            case STATE_SEARCH_CHAT:
                showRoomList();
                break;
        }
    }
}
