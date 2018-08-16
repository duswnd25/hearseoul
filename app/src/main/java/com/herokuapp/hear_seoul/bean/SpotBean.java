package com.herokuapp.hear_seoul.bean;

/**
 * 타입
 * 0 : SK ZonePOI Landmark
 * 1 : User Upload
 */
public class SpotBean  {
    private String title, description, id, imgSrc;
    private double latitude, longitude;
    private boolean visit;
    private int type;

    public SpotBean() {
    }

    public String getType() {
        String value;
        switch (type) {
            case 0:
                value = "SK";
                break;
            case 1:
                value = "USER";
                break;
            default:
                return "USER";
        }
        return value;
    }

    public void setType(String type) {
        switch (type) {
            case "SK":
                this.type = 0;
                break;
            case "USER":
                this.type = 1;
                break;
            default:
                this.type = 1;
                break;
        }
    }

    public boolean isVisit() {
        return visit;
    }

    public void setVisit(boolean visit) {
        this.visit = visit;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }
}
