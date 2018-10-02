package com.moselo.HomingPigeon.Helper;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;

public class HpQRDetection implements Detector.Processor<Barcode> {
    private Activity activity;

    public HpQRDetection(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> barcodes = detections.getDetectedItems();

        if (0 < barcodes.size()) {
            // TODO: 14/09/18 nnti di jalanin sesuai sama flow SOP nya
            activity.runOnUiThread(() -> Toast.makeText(activity, barcodes.valueAt(0).displayValue, Toast.LENGTH_SHORT).show());
        }
    }
}
