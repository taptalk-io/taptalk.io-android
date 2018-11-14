package com.moselo.HomingPigeon.Model;

import java.util.List;

public class TapOrderModel {

    private List<HpProductModel> products;
    private TapRecipientModel recipient;
    private TapCourierModel courier;
    private String orderID;
    private String orderName;
    private String notes;
    private Integer orderStatus;
    private Long orderTime;
    private Long additionalCost;
    private Long discount;
    private Long totalPrice;

    public TapOrderModel() {}

    public List<HpProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<HpProductModel> products) {
        this.products = products;
    }

    public TapRecipientModel getRecipient() {
        return recipient;
    }

    public void setRecipient(TapRecipientModel recipient) {
        this.recipient = recipient;
    }

    public TapCourierModel getCourier() {
        return courier;
    }

    public void setCourier(TapCourierModel courier) {
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
