package com.moselo.HomingPigeon.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageURL {
    @JsonProperty("fullsize") private String fullsize;
    @JsonProperty("thumbnail") private String thumbnail;

    public ImageURL() { }

    public String getFullsize() {
        return fullsize;
    }

    public void setFullsize(String fullsize) {
        this.fullsize = fullsize;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
