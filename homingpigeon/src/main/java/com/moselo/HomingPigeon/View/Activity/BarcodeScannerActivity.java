package com.moselo.HomingPigeon.View.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Fragment.BarcodeScannerFragment;
import com.moselo.HomingPigeon.View.Fragment.ShowQRFragment;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.BarcodeScannerState.STATE_SCAN;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.BarcodeScannerState.STATE_SHOW;

public class BarcodeScannerActivity extends AppCompatActivity {

    private BarcodeScannerFragment fBarcodeScanner;
    private ShowQRFragment fShowQR;
    private TextView tvToolbarTitle;
    private ImageView ivBack;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        // TODO: 14/09/18 make logic for QR code scanner

        fBarcodeScanner = (BarcodeScannerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_scan_qr_code);
        fShowQR = (ShowQRFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_show_qr_code);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(v -> onBackPressed());
        showScanner();
    }

    public void showScanner() {
        state = STATE_SCAN;
        tvToolbarTitle.setText(getResources().getText(R.string.scan_qr_code));
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in,0,0,R.anim.fade_out)
                .show(fBarcodeScanner)
                .hide(fShowQR)
                .commit();
    }

    public void showQR() {
        state = STATE_SHOW;
        tvToolbarTitle.setText(getResources().getText(R.string.show_qr_code));
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in,0,0,R.anim.fade_out)
                .show(fShowQR)
                .hide(fBarcodeScanner)
                .commit();
    }
}
