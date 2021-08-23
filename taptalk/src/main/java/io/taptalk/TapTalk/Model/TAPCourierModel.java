package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPCourierModel {
    private String courierType;
    private Long courierCost;
    private TAPImageURL courierLogo;

    public static TAPCourierModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPCourierModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public String getCourierType() {
        return courierType;
    }

    public void setCourierType(String courierType) {
        this.courierType = courierType;
    }

    public Long getCourierCost() {
        return courierCost;
    }

    public void setCourierCost(Long courierCost) {
        this.courierCost = courierCost;
    }

    public TAPImageURL getCourierLogo() {
        return courierLogo;
    }

    public void setCourierLogo(TAPImageURL courierLogo) {
        this.courierLogo = courierLogo;
    }
}
