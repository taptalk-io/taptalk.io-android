package io.taptalk.TapTalk.View.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import io.taptalk.TapTalk.Helper.TAPQRDetection;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.View.Activity.TAPBarcodeScannerActivity;
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity;
import io.taptalk.TapTalk.View.Activity.TAPScanResultActivity;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;

public class TAPBarcodeScannerFragment extends Fragment {

    private static final String TAG = TAPBarcodeScannerFragment.class.getSimpleName();
    private SurfaceView svScanner;
    private Button btnShowQRCode;

    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private Activity activity;

    public interface ScanListener {
        void onScanSuccess(String textValue);
    }

    public TAPBarcodeScannerFragment() {
        // Required empty public constructor
    }

    public static TAPBarcodeScannerFragment newInstance() {
        TAPBarcodeScannerFragment fragment = new TAPBarcodeScannerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tap_fragment_barcode_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barcodeDetector = new BarcodeDetector.Builder(activity)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        ScanListener scanListener = (String textValue) -> {
            if (!activity.isFinishing() && activity instanceof TAPBaseActivity) {
                TAPScanResultActivity.start(activity, ((TAPBaseActivity) activity).instanceKey, textValue);
                activity.finish();
            }
        };

        barcodeDetector.setProcessor(new TAPQRDetection(activity, scanListener));

        cameraSource = new CameraSource.Builder(activity, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        initView(view);
    }

    private void initView(View view) {
        svScanner = view.findViewById(R.id.sv_scanner);
        btnShowQRCode = view.findViewById(R.id.btn_show_qr_code);

        svScanner.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                startCameraSource();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        btnShowQRCode.setOnClickListener(v -> {
            try {
                ((TAPBarcodeScannerActivity) activity).showQR();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (null != getContext()) {
            btnShowQRCode.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.tap_bg_button_active_ripple));
        }
    }

    @SuppressLint("MissingPermission")
    public void startCameraSource() {
        if (!TAPUtils.hasPermissions(activity, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CAMERA);
        }
        else {
            try {
                cameraSource.start(svScanner.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (TAPUtils.allPermissionsGranted(grantResults)) {
            switch (requestCode) {
                case PERMISSION_CAMERA_CAMERA:
                    startCameraSource();
                    break;
            }
        }
    }
}
