package io.taptalk.TapTalk.View.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.taptalk.Taptalk.R;

public class TAPLoadingMessageFragment extends Fragment {

    private String TAG = TAPLoadingMessageFragment.class.getSimpleName();
    private Activity activity;

    private LinearLayout llConnectionStatus;
    private TextView tvConnectionStatus;

    public TAPLoadingMessageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.tap_fragment_message_loading, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
//        activity.getWindow().setBackgroundDrawable(null);

        llConnectionStatus = view.findViewById(R.id.ll_connection_status);
        tvConnectionStatus = view.findViewById(R.id.tv_connection_status);
    }

    public void setLoadingText(String text) {
        tvConnectionStatus.setText(text);
    }

    public void show() {
        activity.runOnUiThread(() -> llConnectionStatus.setVisibility(View.VISIBLE));
    }

    public void hide() {
        activity.runOnUiThread(() -> llConnectionStatus.setVisibility(View.GONE));
    }
}
