package io.taptalk.TapTalk.View.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.R;

public class TestNavbarFragment extends TapBaseCustomNavigationBarFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tap_cell_user_room, container, false); // TODO: TEST LAYOUT
    }

    public TestNavbarFragment(String instanceKey, TAPRoomModel room, HashMap<String, Object> userInfo) {
        super(instanceKey, room, userInfo);
    }
}
