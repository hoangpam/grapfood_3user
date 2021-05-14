package com.example.grapfood.activity.object;

public class Chef {
    String Emailid, Lname, Postcode, Password, ConfirmPassword ,Area, State,  Mobile, House,City, Fname;
    String getSecureCode;
    // Press Alt+Insert


    public Chef(String emailid, String lname, String postcode, String password, String confirmPassword, String area, String state, String mobile, String house, String city, String fname) {
        Emailid = emailid;
        Lname = lname;
        Postcode = postcode;
        Password = password;
        ConfirmPassword = confirmPassword;
        Area = area;
        State = state;
        Mobile = mobile;
        House = house;
        City = city;
        Fname = fname;
    }

    public Chef() {
    }

    public String getSecureCode() {
        return getSecureCode;
    }

    public void setGetSecureCode(String getSecureCode) {
        this.getSecureCode = getSecureCode;
    }

    public String getEmailid() {
        return Emailid;
    }

    public void setEmailid(String emailid) {
        Emailid = emailid;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        ConfirmPassword = confirmPassword;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getHouse() {
        return House;
    }

    public void setHouse(String house) {
        House = house;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }
}
