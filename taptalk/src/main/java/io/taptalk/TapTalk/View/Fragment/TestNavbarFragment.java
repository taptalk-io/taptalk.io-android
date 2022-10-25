package io.taptalk.TapTalk.View.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.R;

public class TestNavbarFragment extends TapBaseChatRoomCustomNavigationBarFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tap_cell_user_room, container, false); // TODO: TEST LAYOUT
    }

    public TestNavbarFragment(TAPRoomModel room) {
        super(room);
    }

    public TestNavbarFragment(String instanceKey, TAPRoomModel room) {
        super(instanceKey, room);
    }
}
