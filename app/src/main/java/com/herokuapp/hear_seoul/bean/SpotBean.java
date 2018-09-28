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

import java.util.ArrayList;
import java.util.Date;

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
    private String title, description, id, address, time, objectId, phone;
    private int tag;
    private LatLng location;
    private boolean visit;
    private ArrayList<String> imgUrlList;
    private Date updatedAt;
    private boolean isInfluencer;

    public SpotBean() {
    }

    protected SpotBean(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.id = in.readString();
        this.address = in.readString();
        this.time = in.readString();
        this.objectId = in.readString();
        this.phone = in.readString();
        this.tag = in.readInt();
        this.location = in.readParcelable(LatLng.class.getClassLoader());
        this.visit = in.readByte() != 0;
        this.imgUrlList = in.createStringArrayList();
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.isInfluencer = in.readByte() != 0;
    }

    public boolean isInfluencer() {
        return isInfluencer;
    }

    public void setInfluencer(boolean influencer) {
        isInfluencer = influencer;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ArrayList<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(ArrayList<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.id);
        dest.writeString(this.address);
        dest.writeString(this.time);
        dest.writeString(this.objectId);
        dest.writeString(this.phone);
        dest.writeInt(this.tag);
        dest.writeParcelable(this.location, flags);
        dest.writeByte(this.visit ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.imgUrlList);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeByte(this.isInfluencer ? (byte) 1 : (byte) 0);
    }
}
