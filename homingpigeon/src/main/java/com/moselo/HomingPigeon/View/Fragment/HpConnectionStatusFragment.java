package com.moselo.HomingPigeon.View.Fragment;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moselo.HomingPigeon.Interface.HomingPigeonSocketInterface;
import com.moselo.HomingPigeon.Interface.RoomListInterface;
import com.moselo.HomingPigeon.Manager.HpConnectionManager;
import com.moselo.HomingPigeon.Manager.HpNetworkStateManager;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.ViewModel.HpRoomListViewModel;

public class HpConnectionStatusFragment extends Fragment implements HomingPigeonSocketInterface {

    private String TAG = HpConnectionStatusFragment.class.getSimpleName();
    private Activity activity;

    private LinearLayout llConnectionStatus;
    private TextView tvConnectionStatus;
    private ImageView ivConnectionStatus;
    private ProgressBar pbConnecting;

    public HpConnectionStatusFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.hp_fragment_connection_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initConnectionStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HpConnectionManager.getInstance().removeSocketListener(this);
    }

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

    private void initView(View view) {
        activity.getWindow().setBackgroundDrawable(null);

        llConnectionStatus = view.findViewById(R.id.ll_connection_status);
        ivConnectionStatus = view.findViewById(R.id.iv_connection_status);
        tvConnectionStatus = view.findViewById(R.id.tv_connection_status);
        pbConnecting = view.findViewById(R.id.pb_connecting);
    }

    private void initConnectionStatus() {
        HpConnectionManager.getInstance().addSocketListener(this);
        if (!HpNetworkStateManager.getInstance().hasNetworkConnection(getContext()))
            onSocketDisconnected();
        else if (HpConnectionManager.getInstance().getConnectionStatus() == HpConnectionManager.ConnectionStatus.CONNECTED)
            llConnectionStatus.setVisibility(View.GONE);
        else if (HpConnectionManager.getInstance().getConnectionStatus() == HpConnectionManager.ConnectionStatus.CONNECTING)
            onSocketConnecting();
        else if (HpConnectionManager.getInstance().getConnectionStatus() == HpConnectionManager.ConnectionStatus.DISCONNECTED ||
                 HpConnectionManager.getInstance().getConnectionStatus() == HpConnectionManager.ConnectionStatus.NOT_CONNECTED)
            onSocketDisconnected();
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
}
