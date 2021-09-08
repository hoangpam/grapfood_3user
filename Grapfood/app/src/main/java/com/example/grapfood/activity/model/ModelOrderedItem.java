package com.example.grapfood.activity.model;

public class ModelOrderedItem {
    String cost,name,pId,price,quantity;

    public ModelOrderedItem() {
    }

    public ModelOrderedItem(String cost, String name, String pId, String price, String quantity) {
        this.cost = cost;
        this.name = name;
        this.pId = pId;
        this.price = price;
        this.quantity = quantity;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
