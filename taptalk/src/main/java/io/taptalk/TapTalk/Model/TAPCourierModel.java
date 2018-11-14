package io.taptalk.TapTalk.Model;

public class TAPCourierModel {
    private String courierType;
    private Long courierCost;
    private TAPImageURL courierLogo;

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
