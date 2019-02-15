package io.taptalk.TapTalk.Helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import java.util.List;

import io.taptalk.TapTalk.Const.TAPDefaultConstant;

public class TAPAutoStartPermission {

    private static TAPAutoStartPermission instance;

    public static TAPAutoStartPermission getInstance() {
        return null == instance ? instance = new TAPAutoStartPermission() : instance;
    }

    public void showPermissionRequest(Context context) {
        for (Intent intent : TAPDefaultConstant.AUTO_START_INTENTS)
            if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                new TapTalkDialog.Builder(context)
                        .setTitle("Enable AutoStart")
                        .setMessage("Please allow auto start in settings to receive chat notifications from Moselo.")
                        .setPrimaryButtonTitle("Allow")
                        .setPrimaryButtonListener(v -> {
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Log.e("><><><", "showPermissionRequest: ",e );
                                e.printStackTrace();
                            }
                        })
                        .show();
                break;
            }
    }
}
