package io.taptalk.TapTalk.Helper.CustomTabLayout;

import androidx.browser.customtabs.CustomTabsClient;

public interface TAPServiceConnectionCallback {
    /**
     * Called when the service is Connected
     */
    void onServiceConnected(CustomTabsClient client);

    /**
     * Called When the Service is Disconnected
     */
    void onServiceDisconnected();
}
