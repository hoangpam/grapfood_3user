package com.example.grapfood.activity.model;

public class ModelReview {

    //use name spelling of variable as used in sending to firebase
    String uid,rattings,review,timestamp;

    public ModelReview() {
    }

    public ModelReview(String uid, String rattings, String review, String timestamp) {
        this.uid = uid;
        this.rattings = rattings;
        this.review = review;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRattings() {
        return rattings;
    }

    public void setRattings(String rattings) {
        this.rattings = rattings;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
