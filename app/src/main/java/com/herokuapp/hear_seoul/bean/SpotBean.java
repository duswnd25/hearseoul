/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class SpotBean implements Parcelable {
    public static final Creator<SpotBean> CREATOR = new Creator<SpotBean>() {
        @Override
        public SpotBean createFromParcel(Parcel source) {
            return new SpotBean(source);
        }

        @Override
        public SpotBean[] newArray(int size) {
            return new SpotBean[size];
        }
    };
    private String title, description, id, imgSrc, address, time;
    private LatLng location;
    private boolean visit;
    private int type;

    public SpotBean() {
    }

    protected SpotBean(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.id = in.readString();
        this.imgSrc = in.readString();
        this.address = in.readString();
        this.time = in.readString();
        this.location = in.readParcelable(LatLng.class.getClassLoader());
        this.visit = in.readByte() != 0;
        this.type = in.readInt();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isVisit() {
        return visit;
    }

    public void setVisit(boolean visit) {
        this.visit = visit;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.id);
        dest.writeString(this.imgSrc);
        dest.writeString(this.address);
        dest.writeString(this.time);
        dest.writeParcelable(this.location, flags);
        dest.writeByte(this.visit ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
    }
}
