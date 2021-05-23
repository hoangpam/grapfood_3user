package com.example.grapfood.activity.object;

import android.location.Address;

public class Chef {
    String Area,City,CompleteAddress,ConnfirmPassword,EmailId,FirstName,House,LastName,MobileNo,NameShop,Password,State,ImageURL;
    String getSecureCode;
    // Press Alt+Insert


    public Chef(String area, String city, String completeAddress, String connfirmPassword, String emailId, String firstName, String house, String imageURL, String lastName, String mobileNo, String nameShop, String password, String state) {
        Area = area;
        City = city;
        CompleteAddress = completeAddress;
        ConnfirmPassword = connfirmPassword;
        EmailId = emailId;
        FirstName = firstName;
        House = house;
        LastName = lastName;
        MobileNo = mobileNo;
        NameShop = nameShop;
        Password = password;
        State = state;
        this.ImageURL = imageURL;

    }

    public Chef() {
    }

    public String getSecureCode() {
        return getSecureCode;
    }

    public void setGetSecureCode(String getSecureCode) {
        this.getSecureCode = getSecureCode;
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

    public String getCompleteAddress() {
        return CompleteAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        CompleteAddress = completeAddress;
    }

    public String getConnfirmPassword() {
        return ConnfirmPassword;
    }

    public void setConnfirmPassword(String connfirmPassword) {
        ConnfirmPassword = connfirmPassword;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getHouse() {
        return House;
    }

    public void setHouse(String house) {
        House = house;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getNameShop() {
        return NameShop;
    }

    public void setNameShop(String nameShop) {
        NameShop = nameShop;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        this.ImageURL = imageURL;
    }

    public String getGetSecureCode() {
        return getSecureCode;
    }
}
