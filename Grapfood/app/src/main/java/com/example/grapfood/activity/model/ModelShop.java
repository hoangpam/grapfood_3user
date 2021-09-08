package com.example.grapfood.activity.model;

public class ModelShop {
    private String AccountType,UID,MobileNo,DeliveryFree,FirstName,LastName,NameShop,EmailId,City,Area,Password,CompleteAddress,State,ConfirmPassword,House,ImageURL,Latitude,Longitude,Timestamp,Online,ShopOpen;

    public ModelShop() {
    }

    public ModelShop(String accountType, String UID, String mobileNo, String deliveryFree, String firstName, String lastName, String nameShop, String emailId, String city, String area, String password, String completeAddress, String state, String confirmPassword, String house, String imageURL, String latitude, String longitude, String timestamp, String online, String shopOpen) {
        AccountType = accountType;
        this.UID = UID;
        MobileNo = mobileNo;
        DeliveryFree = deliveryFree;
        FirstName = firstName;
        LastName = lastName;
        NameShop = nameShop;
        EmailId = emailId;
        City = city;
        Area = area;
        Password = password;
        CompleteAddress = completeAddress;
        State = state;
        ConfirmPassword = confirmPassword;
        House = house;
        ImageURL = imageURL;
        Latitude = latitude;
        Longitude = longitude;
        Timestamp = timestamp;
        Online = online;
        ShopOpen = shopOpen;
    }

    public String getAccountType() {
        return AccountType;
    }

    public void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getDeliveryFree() {
        return DeliveryFree;
    }

    public void setDeliveryFree(String deliveryFree) {
        DeliveryFree = deliveryFree;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getNameShop() {
        return NameShop;
    }

    public void setNameShop(String nameShop) {
        NameShop = nameShop;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCompleteAddress() {
        return CompleteAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        CompleteAddress = completeAddress;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        ConfirmPassword = confirmPassword;
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

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getOnline() {
        return Online;
    }

    public void setOnline(String online) {
        Online = online;
    }

    public String getShopOpen() {
        return ShopOpen;
    }

    public void setShopOpen(String shopOpen) {
        ShopOpen = shopOpen;
    }
}
