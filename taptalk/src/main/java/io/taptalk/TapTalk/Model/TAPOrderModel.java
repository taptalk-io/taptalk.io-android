package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPOrderModel {

    private TAPUserModel customer;
    private TAPUserModel seller;
    private List<TAPProductModel> products;
    private TAPRecipientModel recipient;
    private TAPCourierModel courier;
    private String orderID;
    private String orderName;
    private String notes;
    private Integer orderStatus;
    private Long orderTime;
    private Long additionalCost;
    private Long discount;
    private Long totalPrice;

    public TAPOrderModel() {
    }

    public static TAPOrderModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPOrderModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public TAPUserModel getCustomer() {
        return customer;
    }

    public void setCustomer(TAPUserModel customer) {
        this.customer = customer;
    }

    public TAPUserModel getSeller() {
        return seller;
    }

    public void setSeller(TAPUserModel seller) {
        this.seller = seller;
    }

    public List<TAPProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<TAPProductModel> products) {
        this.products = products;
    }

    public TAPRecipientModel getRecipient() {
        return recipient;
    }

    public void setRecipient(TAPRecipientModel recipient) {
        this.recipient = recipient;
    }

    public TAPCourierModel getCourier() {
        return courier;
    }

    public void setCourier(TAPCourierModel courier) {
        this.courier = courier;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
    }

    public Long getAdditionalCost() {
        return additionalCost;
    }

    public void setAdditionalCost(Long additionalCost) {
        this.additionalCost = additionalCost;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
