package io.moselo.SampleApps.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

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
            List<String> pathSegments = intent.getData().getPathSegments();
            //Log.e(">>>>", "DeepLinkActivity data: " + data);
            //Log.e(">>>>", "DeepLinkActivity pathSegments: " + TAPUtils.toJsonString(pathSegments));

            if (pathSegments != null && !pathSegments.isEmpty()) {
                // Get room ID from last path segment
                String roomID = pathSegments.get(pathSegments.size() - 1);

                // Get room details from obtained room ID
                TapCoreChatRoomManager.getInstance().getGroupChatRoom(roomID, new TapCoreGetRoomListener() {
                    @Override
                    public void onSuccess(TAPRoomModel room) {
                        //Log.e(">>>>", "DeepLinkActivity get room success: " + room.getName());
                        // Open chat room
                        TapUI.getInstance().openChatRoomWithRoomModel(TapDeepLinkActivity.this, room);
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        //Log.e(">>>>", "DeepLinkActivity get room error: " + errorMessage);
                        // Fallback if room data is not found
                        TapUI.getInstance().openRoomList(TapDeepLinkActivity.this);
                    }
                });
            }
            else {
                // Fallback if room ID is not found
                TapUI.getInstance().openRoomList(TapDeepLinkActivity.this);
            }
        }
        else {
            //Log.e(">>>>", "DeepLinkActivity data null");
            // Fallback if data is null
            TapUI.getInstance().openRoomList(TapDeepLinkActivity.this);
        }

        finish();
    }
}
