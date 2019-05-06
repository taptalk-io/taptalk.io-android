package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import io.taptalk.TapTalk.View.Fragment.TAPBarcodeScannerFragment;
import io.taptalk.TapTalk.View.Fragment.TAPShowQRFragment;
import io.taptalk.Taptalk.R;

public class TAPBarcodeScannerActivity extends TAPBaseActivity {
    private TextView tvToolbarTitle;
    private ImageView ivBack;
    private FrameLayout flToolbar;

    private enum ScanState {
        SCAN, SHOW
    }

    private ScanState state = ScanState.SCAN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_barcode_scanner);

        initView();
        showScanner();
    }

    @Override
    public void onBackPressed() {
        if (ScanState.SHOW == state) {
            showScanner();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
        }
    }

    private void initView() {
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        ivBack = findViewById(R.id.iv_back);
        flToolbar = findViewById(R.id.fl_toolbar);

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    public void showScanner() {
        state = ScanState.SCAN;
        tvToolbarTitle.setText(getResources().getText(R.string.tap_scan_qr_code));
        flToolbar.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.tap_fade_in_fragment, R.animator.tap_fade_out_fragment)
                .replace(R.id.fl_qr_code, TAPBarcodeScannerFragment.newInstance())
                .commit();
    }

    public void showQR() {
        state = ScanState.SHOW;
        tvToolbarTitle.setText(getResources().getText(R.string.tap_show_qr_code));
        flToolbar.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.tap_fade_in_fragment, R.animator.tap_fade_out_fragment)
                .replace(R.id.fl_qr_code, TAPShowQRFragment.newInstance())
                .commit();
    }
}
