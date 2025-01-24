package io.taptalk.TapTalk.View.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;

public abstract class TAPBaseActivity extends AppCompatActivity {

    public String instanceKey = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
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

        final ViewGroup contentView = this.findViewById(android.R.id.content);
        if (contentView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(contentView, new OnApplyWindowInsetsListener() {
                @NonNull
                @Override
                public WindowInsetsCompat onApplyWindowInsets(@NonNull View view, @NonNull WindowInsetsCompat insets) {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    Insets navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                    view.setPadding(0, systemBars.top, 0, navigationBars.bottom);
                    view.setBackgroundColor(ContextCompat.getColor(TAPBaseActivity.this, R.color.tapWhite));
                    return insets;
                }
            });
        }
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
}
