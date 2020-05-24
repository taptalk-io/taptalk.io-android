package io.taptalk.TapTalk.View.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.taptalk.TapTalk.BuildConfig;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Interface.TapTalkSocketInterface;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity;
import io.taptalk.TapTalk.View.Activity.TAPBaseChatActivity;

public class TAPConnectionStatusFragment extends Fragment implements TapTalkSocketInterface {

    private String TAG = TAPConnectionStatusFragment.class.getSimpleName();
    private String instanceKey = "";
    private Activity activity;

    private LinearLayout llConnectionStatus;
    private TextView tvConnectionStatus;
    private ImageView ivConnectionStatus;

    private boolean hideUntilNextConnect;

    private final int padding = TAPUtils.dpToPx(2);

    public TAPConnectionStatusFragment() {
        
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        if (activity instanceof TAPBaseActivity) {
            this.instanceKey = ((TAPBaseActivity) activity).instanceKey;
        } else if (activity instanceof TAPBaseChatActivity) {
            this.instanceKey = ((TAPBaseChatActivity) activity).instanceKey;
        }
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
        TAPConnectionManager.getInstance(instanceKey).removeSocketListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        TAPConnectionManager.getInstance(instanceKey).removeSocketListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG || TapUI.getInstance(instanceKey).isConnectionStatusIndicatorVisible()) {
            initConnectionStatus();
        }
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

        // TODO: 28 November 2019 CONNECTION STATUS SHOWN ONLY IF LOGGING IS ENABLED
        if (TapTalk.isLoggingEnabled) {
            llConnectionStatus.setVisibility(View.GONE);
        }
    }

    private void initConnectionStatus() {
        TAPConnectionManager.getInstance(instanceKey).addSocketListener(this);
        if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(getContext())) {
            onSocketDisconnected();
        } else if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED) {
            llConnectionStatus.setVisibility(View.GONE);
        } else if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTING) {
            onSocketConnecting();
        } else if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.DISCONNECTED ||
                TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.NOT_CONNECTED) {
            onSocketDisconnected();
        }
    }

    private void setStatusConnected() {
        if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() != TAPConnectionManager.ConnectionStatus.CONNECTED) {
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

                new Handler().postDelayed(() -> {
                    if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == TAPConnectionManager.ConnectionStatus.CONNECTED) {
                        llConnectionStatus.setVisibility(View.GONE);
                    }
                }, 500L);
            });
        }
    }

    private void setStatusConnecting() {
        if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(getContext()) ||
                TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() != TAPConnectionManager.ConnectionStatus.CONNECTING ||
                hideUntilNextConnect) {
            return;
        }

        activity.runOnUiThread(() -> {
            llConnectionStatus.setBackgroundResource(R.drawable.tap_bg_status_connecting);
            tvConnectionStatus.setText(R.string.tap_connecting);
            ivConnectionStatus.setImageResource(R.drawable.tap_ic_loading_progress_circle_white);
            ivConnectionStatus.setPadding(padding, padding, padding, padding);
            llConnectionStatus.setVisibility(View.VISIBLE);
            if (null == ivConnectionStatus.getAnimation()) {
                TAPUtils.rotateAnimateInfinitely(getContext(), ivConnectionStatus);
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
                TAPUtils.rotateAnimateInfinitely(getContext(), ivConnectionStatus);
            }
        });

        this.hideUntilNextConnect = false;
    }

    public void hideUntilNextConnect(boolean hide) {
        this.hideUntilNextConnect = hide;
    }
}
