package io.taptalk.TapTalk.View.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.R;

public class TapBaseCustomNavigationBarFragment extends Fragment {

    private String TAG = TapBaseCustomNavigationBarFragment.class.getSimpleName();
    private String instanceKey = "";
    private TAPRoomModel room;
    private HashMap<String, Object> userInfo;

    public TapBaseCustomNavigationBarFragment() {
        
    }

    public TapBaseCustomNavigationBarFragment(String instanceKey, TAPRoomModel room, HashMap<String, Object> userInfo) {
        this.instanceKey = instanceKey;
        this.room = room;
        this.userInfo = userInfo;
    }

    public static TapBaseCustomNavigationBarFragment newInstance(TAPRoomModel room, HashMap<String, Object> userInfo) {
        return new TapBaseCustomNavigationBarFragment("", room, userInfo);
    }

    public static TapBaseCustomNavigationBarFragment newInstance(String instanceKey, TAPRoomModel room, HashMap<String, Object> userInfo) {
        return new TapBaseCustomNavigationBarFragment(instanceKey, room, userInfo);
    }

    public String getInstanceKey() {
        return instanceKey;
    }

    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
    }

    public HashMap<String, Object> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(HashMap<String, Object> userInfo) {
        this.userInfo = userInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tap_cell_empty, container, false); // TODO: TEST LAYOUT
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        initView(view);
//    }
}
