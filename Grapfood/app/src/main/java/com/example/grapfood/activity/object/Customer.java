package com.example.grapfood.activity.object;

public class Customer {
    String Area,City,CompleAddress,ConfirmPassword,EmailId,FistName,House,ImageURL,Lastname,Latitude,Longitude,MobileNo,Password,ShopOpen,State,Timestamp,UID;

    public Customer(String area, String city, String compleAddress, String confirmPassword, String emailId, String fistName, String house, String imageURL, String lastname, String latitude, String longitude, String mobileNo, String password, String shopOpen, String state, String timestamp, String UID) {
        Area = area;
        City = city;
        CompleAddress = compleAddress;
        ConfirmPassword = confirmPassword;
        EmailId = emailId;
        FistName = fistName;
        House = house;
        ImageURL = imageURL;
        Lastname = lastname;
        Latitude = latitude;
        Longitude = longitude;
        MobileNo = mobileNo;
        Password = password;
        ShopOpen = shopOpen;
        State = state;
        Timestamp = timestamp;
        this.UID = UID;
    }

    public Customer() {
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCompleAddress() {
        return CompleAddress;
    }

    public void setCompleAddress(String compleAddress) {
        CompleAddress = compleAddress;
    }

    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        ConfirmPassword = confirmPassword;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getFistName() {
        return FistName;
    }

    public void setFistName(String fistName) {
        FistName = fistName;
    }

    public String getHouse() {
        return House;
    }

    public void setHouse(String house) {
        House = house;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getShopOpen() {
        return ShopOpen;
    }

    public void setShopOpen(String shopOpen) {
        ShopOpen = shopOpen;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
