package com.example.grapfood.activity.model;

public class ModelOrderShop {
     public String devilyveryFee,latitude,longitude,orderBy,orderCost, orderId ,orderStatus,orderTime,orderTo;

    public ModelOrderShop() {
    }

    public ModelOrderShop(String devilyveryFee, String latitude, String longitude, String orderBy, String orderCost, String orderId, String orderStatus, String orderTime, String orderTo) {
        this.devilyveryFee = devilyveryFee;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orderBy = orderBy;
        this.orderCost = orderCost;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
        this.orderTo = orderTo;
    }

    public String getDevilyveryFee() {
        return devilyveryFee;
    }

    public void setDevilyveryFee(String devilyveryFee) {
        this.devilyveryFee = devilyveryFee;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }
}
