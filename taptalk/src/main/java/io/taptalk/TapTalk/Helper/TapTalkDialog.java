package io.taptalk.TapTalk.Helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import io.taptalk.Taptalk.R;

public class TapTalkDialog extends Dialog {

    private static final String TAG = TapTalkDialog.class.getSimpleName();
    protected final Builder mBuilder;
    protected Context context;
    protected TextView title, message, primary, secondary;

    public enum DialogType {DEFAULT, ERROR_DIALOG}

    public TapTalkDialog(Builder builder) {
        super(builder.context);
        mBuilder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (null != getWindow()) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        setContentView(R.layout.tap_dialog);
        title = findViewById(R.id.tv_dialog_title);
        message = findViewById(R.id.tv_dialog_message);
        primary = findViewById(R.id.tv_primary_btn);
        secondary = findViewById(R.id.tv_secondary_btn);

        context = mBuilder.context;
        setTextInTextView(title, mBuilder.dialogTitle);
        setTextInTextView(message, mBuilder.dialogMessage);
        setComponentLayouts(mBuilder.dialogType);

        switch (mBuilder.layout) {
            case 1:
                secondary.setVisibility(View.GONE);
                initiateButton(R.id.tv_primary_btn, mBuilder.textPrimary);
                break;
            case 2:
                secondary.setVisibility(View.VISIBLE);
                initiateButton(R.id.tv_secondary_btn, mBuilder.textSecondary);
                initiateButton(R.id.tv_primary_btn, mBuilder.textPrimary);
                break;
        }
    }

    @Override
    public void show() {
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return;
        }
        super.show();
    }

    private class OnClickListener implements View.OnClickListener {
        private View.OnClickListener listener;
        private boolean isDismissOnClick;

        public OnClickListener(View.OnClickListener listener, boolean isDismissOnClick) {
            this.listener = listener;
            this.isDismissOnClick = isDismissOnClick;
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v);

            if (isDismissOnClick) {
                dismiss();
            }
        }
    }

    private void initiateButton(@IdRes int id, String text) {
        TextView view = findViewById(id);
        if (!text.equals("")) {
            view.setText(text);

            if (view.getId() == R.id.tv_primary_btn && null != mBuilder.primaryListener) {
                view.setOnClickListener(new OnClickListener(mBuilder.primaryListener, mBuilder.primaryIsDismiss));
            } else if (view.getId() == R.id.tv_secondary_btn && null != mBuilder.primaryListener) {
                view.setOnClickListener(new OnClickListener(mBuilder.secondaryListener, mBuilder.secondaryIsDismiss));
            }
        }
    }

    private void setTextInTextView(TextView view, String text) {
        if (text.trim().equals("")) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(text);
        }
    }

    private void setComponentLayouts(DialogType dialogType) {
        Resources res = getContext().getResources();
        switch (dialogType) {
            case ERROR_DIALOG:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                    primary.setBackground(res.getDrawable(R.drawable.tap_bg_dialog_primary_button_error_ripple));
                } else {
                    primary.setBackground(res.getDrawable(R.drawable.tap_bg_dialog_primary_button_error));
                }
                break;
            case DEFAULT:
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                    primary.setBackground(res.getDrawable(R.drawable.tap_bg_dialog_primary_button_success_ripple));
                } else {
                    primary.setBackground(res.getDrawable(R.drawable.tap_bg_dialog_primary_button_success));
                }
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            secondary.setBackground(res.getDrawable(R.drawable.tap_bg_dialog_secondary_button_ripple));
        } else {
            secondary.setBackground(res.getDrawable(R.drawable.tap_bg_dialog_secondary_button));
        }
    }

    public static class Builder {
        protected Context context;
        protected String dialogTitle = "", dialogMessage = "", textSecondary = "", textPrimary = "";
        protected int layout = 1; // 1 for single button (default), 2 for two buttons
        protected boolean cancelable;
        protected DialogType dialogType = DialogType.DEFAULT;
        protected TapTalkDialog dialog;

        // Listener
        protected View.OnClickListener emptyListener = v -> dialog.dismiss();
        protected View.OnClickListener primaryListener = emptyListener;
        protected View.OnClickListener secondaryListener = emptyListener;
        protected boolean primaryIsDismiss = true;
        protected boolean secondaryIsDismiss = true;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String dialogTitle) {
            this.dialogTitle = dialogTitle;
            return this;
        }

        public Builder setMessage(String dialogMessage) {
            this.dialogMessage = dialogMessage;
            return this;
        }

        public Builder setPrimaryButtonTitle(String textPrimary) {
            this.textPrimary = textPrimary;
            return this;
        }

        public Builder setSecondaryButtonTitle(String textSecondary) {
            this.textSecondary = textSecondary;
            layout = 2;
            return this;
        }

        public Builder setDialogType(DialogType type) {
            this.dialogType = type;
            return this;
        }

        public Builder setPrimaryButtonListener(boolean isDismissOnClick, View.OnClickListener primaryListener) {
            this.primaryListener = primaryListener;
            this.primaryIsDismiss = isDismissOnClick;
            return this;
        }

        public Builder setPrimaryButtonListener(View.OnClickListener primaryListener) {
            this.primaryIsDismiss = true;
            this.primaryListener = primaryListener;
            return this;
        }

        public Builder setSecondaryButtonListener(boolean isDismissOnClick, View.OnClickListener secondaryListener) {
            this.secondaryIsDismiss = isDismissOnClick;
            this.secondaryListener = secondaryListener;
            return this;
        }

        public Builder setSecondaryButtonListener(View.OnClickListener secondaryListener) {
            this.secondaryIsDismiss = true;
            this.secondaryListener = secondaryListener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        @UiThread
        public TapTalkDialog build() {
            TapTalkDialog dialog = new TapTalkDialog(this);
            this.dialog = dialog;
            dialog.setCancelable(cancelable);
            return dialog;
        }

        @UiThread
        public TapTalkDialog show() {
            TapTalkDialog dialog = build();
            dialog.show();
            setDialogSize();
            return dialog;
        }

        @UiThread
        private void setDialogSize() {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = TAPUtils.getInstance().getScreenWidth() - TAPUtils.getInstance().dpToPx(90);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
        }
    }
}
