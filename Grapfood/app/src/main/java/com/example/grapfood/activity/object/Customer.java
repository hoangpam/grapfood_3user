package com.example.grapfood.activity.object;

public class Customer {
    String Area,City,ConfirmPassword,EmailId,FirstName,LastName,LocalAddress,MobileNo,Password,Pincode,State;
    public Customer(){
    }
    // Press Alt+insert


    public Customer(String area, String city, String confirmPassword, String emailId, String firstName, String lastName, String localAddress, String mobileNo, String password, String pincode, String state) {
        Area = area;
        City = city;
        ConfirmPassword = confirmPassword;
        EmailId = emailId;
        FirstName = firstName;
        LastName = lastName;
        LocalAddress = localAddress;
        MobileNo = mobileNo;
        Password = password;
        Pincode = pincode;
        State = state;
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

    public String getLocalAddress() {
        return LocalAddress;
    }

    public void setLocalAddress(String localAddress) {
        LocalAddress = localAddress;
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

    public String getPincode() {
        return Pincode;
    }

    public void setPincode(String pincode) {
        Pincode = pincode;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}
