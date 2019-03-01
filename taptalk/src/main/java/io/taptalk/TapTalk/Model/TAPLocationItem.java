package io.taptalk.TapTalk.Model;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

/**
 * Created by Rionaldo on 26/02/18.
 */

public class TAPLocationItem {
    AutocompletePrediction prediction;

    boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public enum MyReturnType {A, B, C, D, FIRST, MIDDLE, LAST, ONLY_ONE}

    MyReturnType myReturnType;

    public MyReturnType getMyReturnType() {
        return myReturnType;
    }

    public void setMyReturnType(MyReturnType myReturnType) {
        this.myReturnType = myReturnType;
    }

    public AutocompletePrediction getPrediction() {
        return prediction;
    }

    public void setPrediction(AutocompletePrediction prediction) {
        this.prediction = prediction;
    }

    Double lon, lat;

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
