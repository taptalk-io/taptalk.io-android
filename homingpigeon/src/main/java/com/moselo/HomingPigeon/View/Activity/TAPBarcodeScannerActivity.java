package com.moselo.HomingPigeon.View.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Fragment.TAPBarcodeScannerFragment;
import com.moselo.HomingPigeon.View.Fragment.TAPShowQRFragment;

public class TAPBarcodeScannerActivity extends TAPBaseActivity {
    private TAPBarcodeScannerFragment fBarcodeScanner;
    private TAPShowQRFragment fShowQR;
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
        setContentView(R.layout.hp_activity_barcode_scanner);

        initView();
        showScanner();
    }

    @Override
    protected void initView() {
        fBarcodeScanner = (TAPBarcodeScannerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_scan_qr_code);
        fShowQR = (TAPShowQRFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_show_qr_code);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        ivBack = findViewById(R.id.iv_back);
        flToolbar = findViewById(R.id.fl_toolbar);

        ivBack.setOnClickListener(v -> onBackPressed());
    }

    public void showScanner() {
        state = ScanState.SCAN;
        tvToolbarTitle.setText(getResources().getText(R.string.scan_qr_code));
        flToolbar.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .show(fBarcodeScanner)
                .hide(fShowQR)
                .commit();
    }

    public void showQR() {
        state = ScanState.SHOW;
        tvToolbarTitle.setText(getResources().getText(R.string.show_qr_code));
        flToolbar.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .show(fShowQR)
                .hide(fBarcodeScanner)
                .commit();
    }
}
