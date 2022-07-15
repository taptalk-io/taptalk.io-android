package io.taptalk.TapTalk.Manager;

//import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.HashMap;

import javax.annotation.Nullable;

import io.taptalk.TapTalk.Helper.TapTalk;

@Deprecated
public class AnalyticsManager {

    private static HashMap<String, AnalyticsManager> instances;
//    private MixpanelAPI mixpanel;

    private String instanceKey = "";

    public static AnalyticsManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            AnalyticsManager instance = new AnalyticsManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, AnalyticsManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
    }

    public AnalyticsManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public void identifyUser() {
//        if (TapTalk.appContext.getPackageName().toLowerCase().startsWith("io.taptalk.taptalksample") && !TapTalk.mixpanelToken.equals("") && null != TAPChatManager.getInstance(instanceKey).getActiveUser()) {
//            mixpanel = MixpanelAPI.getInstance(TapTalk.appContext, TapTalk.mixpanelToken);
//            mixpanel.identify(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID());
//            mixpanel.getPeople().identify(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID());
//            mixpanel.getPeople().set("UserID", TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID());
//            mixpanel.getPeople().set("name", TAPChatManager.getInstance(instanceKey).getActiveUser().getFullname());
//            mixpanel.getPeople().set("UserFullName", TAPChatManager.getInstance(instanceKey).getActiveUser().getFullname());
//            mixpanel.getPeople().set("userPhoneNumber", TAPChatManager.getInstance(instanceKey).getActiveUser().getPhone());
//        }
    }

    public void trackActiveUser() {
//        if (TapTalk.appContext.getPackageName().toLowerCase().startsWith("io.taptalk.taptalksample") && !TapTalk.mixpanelToken.equals("")) {
//            mixpanel = MixpanelAPI.getInstance(TapTalk.appContext, TapTalk.mixpanelToken);
//            JSONObject metadata = generateDefaultData();
//            mixpanel.track("Daily Active Users (DAU)", metadata);
//        }
    }

    public void trackEvent(String keyEvent) {
//        if (TapTalk.appContext.getPackageName().toLowerCase().startsWith("io.taptalk.taptalksample") && !TapTalk.mixpanelToken.equals("")) {
//            mixpanel = MixpanelAPI.getInstance(TapTalk.appContext, TapTalk.mixpanelToken);
//            JSONObject metadata = generateDefaultData();
//            mixpanel.track(keyEvent, metadata);
//        }
    }

    public void trackEvent(String keyEvent, @Nullable HashMap<String, String> additional) {
//        if (TapTalk.appContext.getPackageName().toLowerCase().startsWith("io.taptalk.taptalksample") && !TapTalk.mixpanelToken.equals("")) {
//            mixpanel = MixpanelAPI.getInstance(TapTalk.appContext, TapTalk.mixpanelToken);
//            JSONObject metadata = generateDefaultData();
//            if (null != additional) {
//                for (Map.Entry<String, String> add : additional.entrySet()) {
//                    try {
//                        metadata.put(add.getKey(), add.getValue());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            mixpanel.track(keyEvent, metadata);
//        }
    }

    public void trackErrorEvent(String keyEvent, String errorCode, String errorMessage) {
//        if (TapTalk.appContext.getPackageName().toLowerCase().startsWith("io.taptalk.taptalksample") && !TapTalk.mixpanelToken.equals("")) {
//            mixpanel = MixpanelAPI.getInstance(TapTalk.appContext, TapTalk.mixpanelToken);
//            JSONObject metadata = generateDefaultData();
//            try {
//                metadata.put("errorCode", errorCode);
//                metadata.put("errorMessage", errorMessage);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            mixpanel.track(keyEvent, metadata);
//        }
    }

    public void trackErrorEvent(String keyEvent, String errorCode, String errorMessage, @Nullable HashMap<String, String> additional) {
//        if (TapTalk.appContext.getPackageName().toLowerCase().startsWith("io.taptalk.taptalksample") && !TapTalk.mixpanelToken.equals("")) {
//            mixpanel = MixpanelAPI.getInstance(TapTalk.appContext, TapTalk.mixpanelToken);
//            JSONObject metadata = generateDefaultData();
//            try {
//                metadata.put("errorCode", errorCode);
//                metadata.put("errorMessage", errorMessage);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            if (null != additional) {
//                for (Map.Entry<String, String> add : additional.entrySet()) {
//                    try {
//                        metadata.put(add.getKey(), add.getValue());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            mixpanel.track(keyEvent, metadata);
//        }
    }

    private JSONObject generateDefaultData() {
        JSONObject metadata = new JSONObject();
        try {
            metadata.put("UserID", TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID());
            metadata.put("UserFullName", TAPChatManager.getInstance(instanceKey).getActiveUser().getFullname());
            metadata.put("userPhoneNumber", TAPChatManager.getInstance(instanceKey).getActiveUser().getPhone());
            metadata.put("uuid", TapTalk.getDeviceId());
        } catch (Exception e) {
            return metadata;
        }
        return metadata;
    }
}
