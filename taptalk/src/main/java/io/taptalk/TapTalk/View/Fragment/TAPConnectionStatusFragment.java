package io.taptalk.TapTalk.View.Fragment;

import android.app.Activity;
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
import android.widget.TextView;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Interface.TapTalkSocketInterface;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.Taptalk.R;

public class TAPConnectionStatusFragment extends Fragment implements TapTalkSocketInterface {

    private String TAG = TAPConnectionStatusFragment.class.getSimpleName();
    private Activity activity;

    private LinearLayout llConnectionStatus;
    private TextView tvConnectionStatus;
    private ImageView ivConnectionStatus;

    private boolean hideUntilNextConnect;

    private final int padding = TAPUtils.getInstance().dpToPx(2);

    public TAPConnectionStatusFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.tap_fragment_connection_status, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        TAPConnectionManager.getInstance().removeSocketListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TAPConnectionManager.getInstance().removeSocketListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initConnectionStatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    }

    private void initConnectionStatus() {
        TAPConnectionManager.getInstance().addSocketListener(this);
        // TODO: 22 November 2019 NETWORK STATE MANAGER
        /*if (!TAPNetworkStateManager.getInstance().hasNetworkConnection(getContext()))
            onSocketDisconnected();
        else */if (TAPConnectionManager.getInstance().getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED)
            llConnectionStatus.setVisibility(View.GONE);
        else if (TAPConnectionManager.getInstance().getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTING)
            onSocketConnecting();
        else if (TAPConnectionManager.getInstance().getConnectionStatus() == TAPConnectionManager.ConnectionStatus.DISCONNECTED ||
                 TAPConnectionManager.getInstance().getConnectionStatus() == TAPConnectionManager.ConnectionStatus.NOT_CONNECTED)
            onSocketDisconnected();
    }

    private void setStatusConnected() {
        if (TAPConnectionManager.getInstance().getConnectionStatus() != TAPConnectionManager.ConnectionStatus.CONNECTED) {
            return;
        }

        if (hideUntilNextConnect) {
            this.hideUntilNextConnect = false;
        } else {
            activity.runOnUiThread(() -> {
                llConnectionStatus.setBackgroundResource(R.drawable.tap_bg_status_connected);
                tvConnectionStatus.setText(getString(R.string.tap_connected));
                ivConnectionStatus.setImageResource(R.drawable.tap_ic_checklist_pumpkin);
                ivConnectionStatus.setPadding(0, 0, 0, 0);
                llConnectionStatus.setVisibility(View.VISIBLE);
                ivConnectionStatus.clearAnimation();

                new Handler().postDelayed(() -> llConnectionStatus.setVisibility(View.GONE), 500L);
            });
        }
    }

    private void setStatusConnecting() {
        // TODO: 22 November 2019 NETWORK STATE MANAGER
//        if (!TAPNetworkStateManager.getInstance().hasNetworkConnection(getContext()) ||
//                TAPConnectionManager.getInstance().getConnectionStatus() != TAPConnectionManager.ConnectionStatus.CONNECTING ||
//                hideUntilNextConnect) {
//            return;
//        }

        activity.runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.tap_bg_status_connecting);
            tvConnectionStatus.setText(R.string.tap_connecting);
            ivConnectionStatus.setImageResource(R.drawable.tap_ic_loading_progress_circle_white);
            ivConnectionStatus.setPadding(padding, padding, padding, padding);
            llConnectionStatus.setVisibility(View.VISIBLE);
            if (null == ivConnectionStatus.getAnimation()) {
                TAPUtils.getInstance().rotateAnimateInfinitely(getContext(), ivConnectionStatus);
            }
        });
    }

    private void setStatusWaitingForNetwork() {
        if (hideUntilNextConnect) {
            return;
        }

        activity.runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.tap_bg_status_offline);
            tvConnectionStatus.setText(R.string.tap_waiting_for_network);
            ivConnectionStatus.setImageResource(R.drawable.tap_ic_loading_progress_circle_white);
            ivConnectionStatus.setPadding(padding, padding, padding, padding);
            llConnectionStatus.setVisibility(View.VISIBLE);
            if (null == ivConnectionStatus.getAnimation()) {
                TAPUtils.getInstance().rotateAnimateInfinitely(getContext(), ivConnectionStatus);
            }
        });

        this.hideUntilNextConnect = false;
    }

    public void hideUntilNextConnect(boolean hide) {
        this.hideUntilNextConnect = hide;
    }
}
