package io.taptalk.TapTalk.View.Activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;

import java.util.List;

public abstract class TAPBaseActivity extends AppCompatActivity {

    public String instanceKey = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instanceKey = getIntent().getStringExtra(INSTANCE_KEY);

//        switch (TapTalk.getTapTalkScreenOrientation()) {
//            case TapTalkOrientationPortrait:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                break;
//            case TapTalkOrientationLandscape:
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                break;
//        }

        // Show/hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            if (this instanceof TapUIChatActivity) {
                Boolean allActionBarEnabled = TapUI.getInstance(instanceKey).isAllTapTalkActivityActionBarEnabled();
                Boolean chatRoomActionBarEnabled = TapUI.getInstance(instanceKey).isTapTalkChatActivityActionBarEnabled();
                Boolean actionBarEnabled = null;
                if (null != allActionBarEnabled && null != chatRoomActionBarEnabled) {
                    actionBarEnabled = allActionBarEnabled || chatRoomActionBarEnabled;
                } else if (null != allActionBarEnabled) {
                    actionBarEnabled = allActionBarEnabled;
                } else if (null != chatRoomActionBarEnabled) {
                    actionBarEnabled = chatRoomActionBarEnabled;
                }
                if (null != actionBarEnabled) {
                    if (actionBarEnabled) {
                        actionBar.show();
                    } else {
                        actionBar.hide();
                    }
                }
            } else {
                Boolean actionBarEnabled = TapUI.getInstance(instanceKey).isAllTapTalkActivityActionBarEnabled();
                if (null != actionBarEnabled) {
                    actionBar = getSupportActionBar();
                    if (actionBarEnabled) {
                        actionBar.show();
                    } else {
                        actionBar.hide();
                    }
                }
            }
        }

        applyWindowInsets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TapUI.getInstance(instanceKey).setCurrentForegroundTapTalkActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TAPUtils.dismissKeyboard(this);
        TapUI.getInstance(instanceKey).setCurrentForegroundTapTalkActivity(null);
    }

    /**
     * Handle Edge-to-Edge for Android 15 (VANILLA_ICE_CREAM) / SDK 35
     */

    public void applyWindowInsets() {
        applyWindowInsets(ContextCompat.getColor(this, R.color.tapWhite));
    }

    public void applyWindowInsets(@ColorInt int insetBackgroundColor) {
        final ViewGroup contentView = this.findViewById(android.R.id.content);
        if (contentView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(contentView, new OnApplyWindowInsetsListener() {
                @NonNull
                @Override
                public WindowInsetsCompat onApplyWindowInsets(@NonNull View view, @NonNull WindowInsetsCompat insets) {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    Insets navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                    boolean imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
                    int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
                    view.setPadding(
                        navigationBars.left,
                        systemBars.top,
                        navigationBars.right,
                        imeVisible ? imeHeight : navigationBars.bottom
                    );
                    GradientDrawable gradient = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {
                            insetBackgroundColor,
                            insetBackgroundColor,
                            Color.TRANSPARENT,
                            Color.TRANSPARENT
                        }
                    );
                    gradient.setCornerRadius(0f);
                    view.setBackground(gradient);
                    return insets;
                }
            });

            ViewCompat.setWindowInsetsAnimationCallback(
                contentView,
                new WindowInsetsAnimationCompat.Callback(WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_STOP) {

                    float startBottom;
                    float endBottom;

                    @Override
                    public void onPrepare(@NonNull WindowInsetsAnimationCompat animation) {
                        startBottom = contentView.getBottom();
                    }

                    @NonNull
                    @Override
                    public WindowInsetsAnimationCompat.BoundsCompat onStart(@NonNull WindowInsetsAnimationCompat animation, @NonNull WindowInsetsAnimationCompat.BoundsCompat bounds) {
                        endBottom = contentView.getBottom();
                        return bounds;
                    }

                    @NonNull
                    @Override
                    public WindowInsetsCompat onProgress(@NonNull WindowInsetsCompat insets, @NonNull List<WindowInsetsAnimationCompat> runningAnimations) {
                        // Find an IME animation.
                        WindowInsetsAnimationCompat imeAnimation = null;
                        for (WindowInsetsAnimationCompat animation : runningAnimations) {
                            if ((animation.getTypeMask() & WindowInsetsCompat.Type.ime()) != 0) {
                                imeAnimation = animation;
                                break;
                            }
                        }
                        if (imeAnimation != null) {
                            // Offset the view based on the interpolated fraction of the IME animation.
                            contentView.setTranslationY((startBottom - endBottom) * (1 - imeAnimation.getInterpolatedFraction()));
                        }
                        return insets;
                    }
                }
            );
        }
    }
}
