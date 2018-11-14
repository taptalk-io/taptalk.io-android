package com.moselo.HomingPigeon.Model;

import android.arch.persistence.room.Ignore;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class HpProductModel {

    @JsonProperty("productName") private String name;
    @JsonProperty("thumbnailURL") private HpImageURL thumbnail;
    @JsonProperty("subcategory") private HpPairIdNameModel subcategory;
    @JsonProperty("productID") private String productID;
    @JsonProperty("price") private Long price;
    @JsonProperty("type") private String type;
    private String description;
    private float rating;
    private int quantity;

    @Ignore
    public HpProductModel(String name, HpImageURL thumbnail, HpPairIdNameModel subcategory, String productID, Long price, String type) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.subcategory = subcategory;
        this.productID = productID;
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

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
