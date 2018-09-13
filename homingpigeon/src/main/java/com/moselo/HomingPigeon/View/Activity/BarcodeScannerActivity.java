package com.moselo.HomingPigeon.View.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Fragment.BarcodeScannerFragment;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.BarcodeScannerState.STATE_SCAN;

public class BarcodeScannerActivity extends AppCompatActivity {

    private BarcodeScannerFragment fBarcodeScanner;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        fBarcodeScanner = (BarcodeScannerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_scan_qr_code);
        showScanner();
    }

    private void showScanner() {
        state = STATE_SCAN;
        getSupportFragmentManager()
                .beginTransaction()
                .show(fBarcodeScanner)
                .commit();
    }
}
