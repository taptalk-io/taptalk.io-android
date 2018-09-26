package com.moselo.HomingPigeon.Model;

import android.arch.persistence.room.Ignore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class ProductModel {

    @JsonProperty("productName") private String name;
    @JsonProperty("thumbnailURL") private ImageURL thumbnail;
    @JsonProperty("subcategory") private PairIdNameModel subcategory;
    @JsonProperty("productID") private String prodID;
    @JsonProperty("price") private Long price;
    @JsonProperty("type") private String type;

    @Ignore
    public ProductModel(String name, ImageURL thumbnail, PairIdNameModel subcategory, String prodID, Long price, String type) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.subcategory = subcategory;
        this.prodID = prodID;
        this.price = price;
        this.type = type;
    }

    public ProductModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageURL getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ImageURL thumbnail) {
        this.thumbnail = thumbnail;
    }

    public PairIdNameModel getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(PairIdNameModel subcategory) {
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
