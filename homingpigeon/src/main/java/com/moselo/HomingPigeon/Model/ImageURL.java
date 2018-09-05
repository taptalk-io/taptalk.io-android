package com.moselo.HomingPigeon.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "x1",
        "x2",
        "x3",
})


public class ImageURL {
    @JsonProperty("x1") private String x1;
    @JsonProperty("x2") private String x2;
    @JsonProperty("x3") private String x3;

    public ImageURL() { }

    public String getX1() {
        return x1;
    }

    public void setX1(String x1) {
        this.x1 = x1;
    }

    public String getX2() {
        return x2;
    }

    public void setX2(String x2) {
        this.x2 = x2;
    }

    public String getX3() {
        return x3;
    }

    public void setX3(String x3) {
        this.x3 = x3;
    }
}
