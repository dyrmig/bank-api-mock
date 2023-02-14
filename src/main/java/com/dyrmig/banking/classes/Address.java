package com.dyrmig.banking.classes;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String streetName;
    private String streetNumber;
    private String city;
    private String province;
    private String postalCode;
    private String country;

    public Address() {
    }

    public Address(String streetName, String streetNumber, String city, String province, String postalCode, String country) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
