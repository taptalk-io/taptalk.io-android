package com.moselo.HomingPigeon.Model;

import android.arch.persistence.room.Ignore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class HpProductModel {

    @JsonProperty("productName") private String name;
    @JsonProperty("thumbnailURL") private HpImageURL thumbnail;
    @JsonProperty("subcategory") private HpPairIdNameModel subcategory;
    @JsonProperty("productID") private String prodID;
    @JsonProperty("price") private Long price;
    @JsonProperty("type") private String type;

    @Ignore
    public HpProductModel(String name, HpImageURL thumbnail, HpPairIdNameModel subcategory, String prodID, Long price, String type) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.subcategory = subcategory;
        this.prodID = prodID;
        this.price = price;
        this.type = type;
    }

    public HpProductModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HpImageURL getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(HpImageURL thumbnail) {
        this.thumbnail = thumbnail;
    }

    public HpPairIdNameModel getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(HpPairIdNameModel subcategory) {
        this.subcategory = subcategory;
    }

    public String getProdID() {
        return prodID;
    }

    public void setProdID(String prodID) {
        this.prodID = prodID;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
