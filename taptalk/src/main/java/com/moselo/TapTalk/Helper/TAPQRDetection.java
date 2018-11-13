package com.moselo.TapTalk.Helper;

import android.app.Activity;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.moselo.HomingPigeon.View.Fragment.TAPBarcodeScannerFragment;

public class TAPQRDetection implements Detector.Processor<Barcode> {
    private Activity activity;
    private TAPBarcodeScannerFragment.ScanListener listener;

    public TAPQRDetection(Activity activity, TAPBarcodeScannerFragment.ScanListener listener) {
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
