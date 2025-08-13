package io.taptalk.TapTalk.Helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.UiThread;

import io.taptalk.TapTalk.R;

public class TapLoadingDialog extends Dialog {

    private final Builder builder;
    protected Context context;

    private ImageView ivLoading;
    private TextView tvLoading;

    public TapLoadingDialog(Builder builder) {
        super(builder.context);
        this.builder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tap_loading_dialog);

        context = builder.context;

        initView();
    }

    private void initView() {
        if (null != getWindow()) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ivLoading = findViewById(R.id.iv_loading);
        tvLoading = findViewById(R.id.tv_loading);

        TAPUtils.rotateAnimateInfinitely(getContext(), ivLoading);
        if (builder.loadingText != null && !builder.loadingText.isEmpty()) {
            tvLoading.setText(builder.loadingText);
        }
    }

//    @UiThread
//    private void setDialogSize() {
//        if (null == getWindow()) {
//            return;
//        }
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(getWindow().getAttributes());
//        lp.width = TAPUtils.getScreenWidth();
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        getWindow().setAttributes(lp);
//    }

    @Override
    public void show() {
        if (context == null || (context instanceof Activity && ((Activity) context).isFinishing())) {
            return;
        }
//        setDialogSize();
        try {
            super.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Builder {
        protected Context context;
        TapLoadingDialog dialog;
        String loadingText = "";
        boolean isCancelable = false;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String loadingText) {
            this.loadingText = loadingText;
            return this;
        }

        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        @UiThread
        public TapLoadingDialog build() {
            TapLoadingDialog dialog = new TapLoadingDialog(this);
            this.dialog = dialog;
            dialog.context = this.context;
            dialog.setCancelable(isCancelable);
            return dialog;
        }

        @UiThread
        public TapLoadingDialog show() {
            TapLoadingDialog dialog = build();
            this.dialog = dialog;
            dialog.show();
//            setDialogSize();
            return dialog;
        }

        @UiThread
        public void dismiss() {
            if (null != dialog) {
                dialog.dismiss();
            }
        }

//        @UiThread
//        private void setDialogSize() {
//            if (null == dialog.getWindow()) {
//                return;
//            }
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            lp.copyFrom(dialog.getWindow().getAttributes());
//            lp.width = TAPUtils.getScreenWidth();
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            dialog.getWindow().setAttributes(lp);
//        }

        public boolean isShowing() {
            return null != dialog && dialog.isShowing();
        }
    }
}
