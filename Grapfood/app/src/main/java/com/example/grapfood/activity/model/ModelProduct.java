package com.example.grapfood.activity.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class ModelProduct implements Serializable {
    private String  productId,productTitle,productDesciptions,productCategory,productQuanlity,productIcon,originalPrice,
            discountPrice,discountNote,discountAvailable,timestamp,uid;



    String mText;
    public ModelProduct(String productId, String productTitle, String productDesciptions, String productCategory, String productQuanlity, String productIcon, String originalPrice, String discountPrice, String discountNote, String discountAvailable, String timestamp, String uid) {
        if(productCategory.trim().equals("")){
            productCategory = "Không tìm thấy thể loại sẩn phẩm";
        }
        if(productTitle.trim().equals("")){
            productTitle = "Không tìm thấy tên sẩn phẩm";
        }
        this.productId = productId;
        this.productTitle = productTitle;
        this.productDesciptions = productDesciptions;
        this.productCategory = productCategory;
        this.productQuanlity = productQuanlity;
        this.productIcon = productIcon;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.discountNote = discountNote;
        this.discountAvailable = discountAvailable;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public ModelProduct() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDesciptions() {
        return productDesciptions;
    }

    public void setProductDesciptions(String productDesciptions) {
        this.productDesciptions = productDesciptions;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductQuanlity() {
        return productQuanlity;
    }

    public void setProductQuanlity(String productQuanlity) {
        this.productQuanlity = productQuanlity;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return getProductTitle();
    }

    @Exclude
    public String getKey() {
        return uid;
    }
    @Exclude
    public void setKey(String key) {
        this.uid = key;
    }
}
