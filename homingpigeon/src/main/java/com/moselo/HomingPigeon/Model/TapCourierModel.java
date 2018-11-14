package com.moselo.HomingPigeon.Model;

public class TapCourierModel {
    private String courierType;
    private Long courierCost;
    private HpImageURL courierLogo;

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

    public HpImageURL getCourierLogo() {
        return courierLogo;
    }

    public void setCourierLogo(HpImageURL courierLogo) {
        this.courierLogo = courierLogo;
    }
}
