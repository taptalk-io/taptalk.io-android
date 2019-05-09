package io.taptalk.TapTalk.Helper.CustomTabLayout;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

import java.util.List;

/**
 * Helper ini tugasnya untuk manage koneksi ke Custom Tab Service Google Chrome
 */
public class TAPCustomTabActivityHelper implements TAPServiceConnectionCallback {

    private CustomTabsSession mCustomTabsSession;
    private CustomTabsClient mClient;
    private CustomTabsServiceConnection mConnection;
    private ConnectionCallback mConnectionCallback;

    /**
     * Callback untuk saat servicenya connect atau disconnect
     * digunakan untuk ngehandle UI Change sesuai dengan kondisi Servicenya
     */
    public interface ConnectionCallback {
        /**
         * Dipanggil pas Servicenya connect
         */
        void onCustomTabsConnected();

        /**
         * Dipanggil pas Servicenya Disconnect
         */
        void onCustomTabsDisconnected();
    }

    /**
     * Digunain kalau misalnya Custom Tabsnya not available
     */
    public interface CustomTabFallback {
        /**
         * @param activity activity yang mau buka browser
         * @param uri link url yang mau di buka
         */
        void openUri(Activity activity, Uri uri);
    }

    @Override
    public void onServiceConnected(CustomTabsClient client) {
        mClient = client;
        mClient.warmup(0L);
        if (null != mConnectionCallback) mConnectionCallback.onCustomTabsConnected();
    }

    @Override
    public void onServiceDisconnected() {
        mClient = null;
        mCustomTabsSession = null;
        if (null != mConnectionCallback) mConnectionCallback.onCustomTabsDisconnected();
    }

    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent,
                                     Uri uri, CustomTabFallback fallback) {
        String packageName = TAPCustomTabHelper.getPackageNameToUse(activity);

        //If we cant find a package name, it means theres no browser that supports
        //Chrome Custom Tabs installed. So, we fallback to the webview
        if (null == packageName) {
            if (null != fallback) {
                fallback.openUri(activity, uri);
            }
        } else {
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);
        }
    }

    /**
     * Unbinds the Activity from the Custom Tabs Service.
     * @param activity the activity that is connected to the service.
     */
    public void unbindCustomTabsService(Activity activity) {
        if (null == mConnection) return;
        activity.unbindService(mConnection);
        mClient = null;
        mCustomTabsSession = null;
        mConnection = null;
    }

    /**
     * Creates or retrieves an exiting CustomTabsSession.
     *
     * @return a CustomTabsSession.
     */
    public CustomTabsSession getSession() {
        if (null == mClient) {
            mCustomTabsSession = null;
        } else if (null == mCustomTabsSession) {
            mCustomTabsSession = mClient.newSession(null);
        }
        return mCustomTabsSession;
    }

    /**
     * Register a Callback to be called when connected or disconnected from the Custom Tabs Service.
     * @param connectionCallback
     */
    public void setConnectionCallback(ConnectionCallback connectionCallback) {
        this.mConnectionCallback = connectionCallback;
    }

    /**
     * Binds the Activity to the Custom Tabs Service.
     * @param activity the activity to be binded to the service.
     */
    public void bindCustomTabsService(Activity activity) {
        if (null != mClient) return;

        String packageName = TAPCustomTabHelper.getPackageNameToUse(activity);
        if (null == packageName) return;

        mConnection = new TAPServiceConnection(this);
        CustomTabsClient.bindCustomTabsService(activity, packageName, mConnection);
    }

    /**
     * @see {@link CustomTabsSession#mayLaunchUrl(Uri, Bundle, List)}.
     * @return true if call to mayLaunchUrl was accepted.
     */
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        if (null == mClient) return false;

        CustomTabsSession session = getSession();
        if (null == session) return false;

        return session.mayLaunchUrl(uri, extras, otherLikelyBundles);
    }
}
