package io.taptalk.TapTalk.View.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity;

public class TapBaseChatRoomCustomNavigationBarFragment extends Fragment {

    private TAPRoomModel room;

    public TapBaseChatRoomCustomNavigationBarFragment() {
        
    }

    public TapBaseChatRoomCustomNavigationBarFragment(TAPRoomModel room) {
        this.room = room;
    }

    @Nullable
    public String getInstanceKey() {
        if (getActivity() != null && getActivity() instanceof TAPBaseActivity) {
            return ((TAPBaseActivity)getActivity()).instanceKey;
        }
        return null;
    }

    public TAPRoomModel getRoom() {
        return room;
    }

    public void setRoom(TAPRoomModel room) {
        this.room = room;
    }

    public void onBackPressed() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tap_cell_empty, container, false);
    }
}
