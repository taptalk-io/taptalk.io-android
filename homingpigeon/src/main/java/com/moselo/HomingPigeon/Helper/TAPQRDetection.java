package com.moselo.HomingPigeon.Helper;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.moselo.HomingPigeon.View.Fragment.HpBarcodeScannerFragment;

public class TAPQRDetection implements Detector.Processor<Barcode> {
    private Activity activity;
    private HpBarcodeScannerFragment.ScanListener listener;

    public TAPQRDetection(Activity activity, HpBarcodeScannerFragment.ScanListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> barcodes = detections.getDetectedItems();

        if (0 < barcodes.size()) {
            activity.runOnUiThread(() -> listener.onScanSuccess(barcodes.valueAt(0).displayValue));
        }
    }
}
