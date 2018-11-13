package com.moselo.TapTalk.Helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.moselo.HomingPigeon.R;

public class TapTalkDialog extends Dialog {

    private static final String TAG = TapTalkDialog.class.getSimpleName();
    protected final Builder mBuilder;
    protected TextView title, message, primary, secondary;

    public TapTalkDialog(Builder builder) {
        super(builder.context);
        mBuilder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
        }

        setContentView(R.layout.tap_dialog);
        title = findViewById(R.id.tv_dialog_title);
        message = findViewById(R.id.tv_dialog_message);
        primary = findViewById(R.id.tv_primary_btn);
        secondary = findViewById(R.id.tv_secondary_btn);

        setTextInTextView(title, mBuilder.dialogTitle);
        setTextInTextView(message, mBuilder.dialogMessage);

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

    private class OnClickListener implements View.OnClickListener{
        private View.OnClickListener listener;
        private boolean isDismissOnClick;

        public OnClickListener(View.OnClickListener listener, boolean isDismissOnClick) {
            this.listener = listener;
            this.isDismissOnClick = isDismissOnClick;
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v);

            if (isDismissOnClick)
                dismiss();
        }
    }

    private void initiateButton(@IdRes int id, String text) {
        TextView view = findViewById(id);
        if (!text.equals("")) {
            view.setText(text);

            if (view.getId() == R.id.tv_primary_btn && null != mBuilder.primaryListener)
                view.setOnClickListener(new OnClickListener(mBuilder.primaryListener, mBuilder.primaryIsDismiss));
            else if (view.getId() == R.id.tv_secondary_btn && null != mBuilder.primaryListener)
                view.setOnClickListener(new OnClickListener(mBuilder.secondaryListener, mBuilder.secondaryIsDismiss));
        }
    }

    private void setTextInTextView(TextView view, String text) {
        if (text.trim().equals("")) view.setVisibility(View.GONE);
        else view.setText(text);
    }

    public static class Builder {
        protected Context context;

        protected String dialogTitle = "", dialogMessage = "", textSecondary = "", textPrimary = "";
        protected int layout = 1; //1 one Button, 2 two button
        protected TapTalkDialog dialog;

        //listener
        protected View.OnClickListener emptyListener = v -> {};
        protected View.OnClickListener primaryListener = emptyListener;
        protected View.OnClickListener secondaryListener = emptyListener;
        protected boolean primaryIsDismiss = false;
        protected boolean secondaryIsDismiss = false;

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

        @UiThread
        public TapTalkDialog build() {
            TapTalkDialog dialog = new TapTalkDialog(this);
            this.dialog = dialog;
            return dialog;
        }

        @UiThread
        public TapTalkDialog show() {
            TapTalkDialog dialog = build();
            dialog.show();
            return dialog;
        }
    }
}
