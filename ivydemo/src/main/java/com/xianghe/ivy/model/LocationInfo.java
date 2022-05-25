package com.xianghe.ivy.model;

import java.io.Serializable;

public class LocationInfo implements Serializable {
    private String province;
    private String city;
    private String title;
    private String adName;
    private String address;
    private String district;
    private double latitude;
    private double longTitude;



    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongTitude() {
        return longTitude;
    }

    public void setLongTitude(double longTitude) {
        this.longTitude = longTitude;
    }

}
