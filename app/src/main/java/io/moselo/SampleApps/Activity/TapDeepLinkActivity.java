package io.moselo.SampleApps.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Listener.TapCoreGetRoomListener;
import io.taptalk.TapTalk.Manager.TapCoreChatRoomManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.TAPRoomModel;
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
//                    Log.e(">>>>", "DeepLinkActivity pathSegments: " + TAPUtils.toJsonString(pathSegments));

                    if (pathSegments != null && !pathSegments.isEmpty()) {
                        // Get room ID from last path segment
                        String roomID = pathSegments.get(pathSegments.size() - 1);

                        // Get room details from obtained room ID
                        TapCoreChatRoomManager.getInstance().getGroupChatRoom(roomID, new TapCoreGetRoomListener() {
                            @Override
                            public void onSuccess(TAPRoomModel room) {
//                                Log.e(">>>>", "DeepLinkActivity get room success: " + room.getName());
                                // Open chat room
                                TapUI.getInstance().openChatRoomWithRoomModel(TapDeepLinkActivity.this, room);
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {
//                                Log.e(">>>>", "DeepLinkActivity get room error: " + errorMessage);
                                // Fallback if room data is not found
                                openRoomList();
                            }
                        });
                    }
                    else {
//                        Log.e(">>>>", "DeepLinkActivity room ID not found");
                        // Fallback if room ID is not found
                        openRoomList();
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

    private void openRoomList() {
        if (TapUI.getInstance().getCurrentTapTalkRoomListFragment() == null) {
            TapUI.getInstance().openRoomList(this);
        }
    }

    private void openLoginActivity() {
        TAPLoginActivity.start(this, "");
    }

    private void openFallbackActivity() {
        if (TapUI.getInstance().getCurrentTapTalkRoomListFragment() == null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
