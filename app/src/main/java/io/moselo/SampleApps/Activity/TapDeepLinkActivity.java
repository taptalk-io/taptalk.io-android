package io.moselo.SampleApps.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import io.moselo.SampleApps.SampleApplication;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalkSample.R;

public class TapDeepLinkActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
//            Log.e(">>>>", "DeepLinkActivity data: " + data);
            if (data.getHost().equals("web.taptalk.io")) {
                if (TapTalk.isAuthenticated()) {
                    List<String> pathSegments = data.getPathSegments();
//                    Log.e(">>>>", "DeepLinkActivity pathSegments: " + pathSegments);

                    if (pathSegments != null && !pathSegments.isEmpty()) {
                        // Get room ID from last path segment
                        String roomID = pathSegments.get(pathSegments.size() - 1);
//                        Log.e(">>>>", "DeepLinkActivity open with room ID: " + roomID);
                        openRoomList(roomID);
                    }
                    else {
//                        Log.e(">>>>", "DeepLinkActivity room ID not found, open room list");
                        // Fallback if room ID is not found
                        openRoomList("");
                    }
                }
                else {
//                    Log.e(">>>>", "DeepLinkActivity user is not logged in");
                    // User is not logged in
                    openLoginActivity();
                }
            }
            else {
//                Log.e(">>>>", "DeepLinkActivity url is not chat");
                // Fallback if tapped url is not chat
                openFallbackActivity();
            }
        }
        else {
//            Log.e(">>>>", "DeepLinkActivity data null");
            // Fallback if data is null
            openFallbackActivity();
        }

        finish();
    }

    private void openRoomList(String roomID) {
        TapUI.getInstance().openRoomList(this, roomID);
    }

    private void openLoginActivity() {
        if (getApplication() != null && getApplication() instanceof SampleApplication) {
            if (!((SampleApplication) getApplication()).loginActivityExists) {
                TAPLoginActivity.start(this, "");
            }
        }
    }

    private void openFallbackActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
