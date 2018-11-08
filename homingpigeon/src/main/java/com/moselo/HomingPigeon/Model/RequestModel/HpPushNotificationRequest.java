package com.moselo.HomingPigeon.Model.RequestModel;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HpPushNotificationRequest {
    @JsonProperty("fcmToken") private String fcmToken;
    //ini nnti kalau boleh di hapus, di hapus aja
    @Nullable @JsonProperty("apnToken") private String apnToken;

    public HpPushNotificationRequest(String fcmToken, @Nullable String apnToken) {
        this.fcmToken = fcmToken;
        this.apnToken = apnToken;
    }

    public HpPushNotificationRequest() {
    }

    public static HpPushNotificationRequest Builder(String fcmToken) {
        //apnToken di biarin kosong karena di android cman pake fcmToken
        //apnToken itu buat di iOS
        //kalau apn Token boleh ga dikirim, di hapus aja
        return new HpPushNotificationRequest(fcmToken, null);
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Nullable
    public String getApnToken() {
        return apnToken;
    }

    public void setApnToken(@Nullable String apnToken) {
        this.apnToken = apnToken;
    }
}
