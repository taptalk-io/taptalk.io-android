package io.taptalk.TapTalk.View.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.taptalk.TapTalk.Helper.QRCode.BarcodeFormat;
import io.taptalk.TapTalk.Helper.QRCode.BitMatrix;
import io.taptalk.TapTalk.Helper.QRCode.MultiFormatWriter;
import io.taptalk.TapTalk.Helper.QRCode.WriterException;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.R;
import io.taptalk.TapTalk.View.Activity.TAPBarcodeScannerActivity;
import io.taptalk.TapTalk.View.Activity.TAPBaseActivity;

public class TAPShowQRFragment extends Fragment {

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    private Bitmap bitmap;

    private ImageView ivQRCode;
    private Button btnScanQRCode;

    private Activity activity;

    public TAPShowQRFragment() {
    }

    public static TAPShowQRFragment newInstance() {
        TAPShowQRFragment fragment = new TAPShowQRFragment();
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
        return inflater.inflate(R.layout.tap_fragment_show_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivQRCode = view.findViewById(R.id.iv_qr_code);
        btnScanQRCode = view.findViewById(R.id.btn_scan_qr_code);

        try {
            if (activity instanceof TAPBaseActivity) {
                bitmap = encodeAsBitmap("id:" + TAPChatManager.getInstance(((TAPBaseActivity) activity).instanceKey).getActiveUser().getUserID());
                ivQRCode.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnScanQRCode.setOnClickListener(v -> {
            try {
                ((TAPBarcodeScannerActivity) getActivity()).showScanner();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != getContext()) {
            btnScanQRCode.setBackground(getContext().getDrawable(R.drawable.tap_bg_button_active_ripple));
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 500, 500, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    }
}
