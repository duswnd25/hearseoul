/*
 * Copyright (c) 2018. YeonJung Kim
 *
 *  GitHub : @duswnd25
 *  Site   : https://yeonjung.herokuapp.com/
 */

package com.herokuapp.hear_seoul.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class EventBean implements Parcelable {
    private String cultureCode, sponsor, subCode, codeName, title, startDate, endDate, time, place, orgLink, mainImg, homepage, useTarget, useFee, inquery, program, content, gcode;

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getCultureCode() {
        return cultureCode;
    }

    public void setCultureCode(String cultureCode) {
        this.cultureCode = cultureCode;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getOrgLink() {
        return orgLink;
    }

    public void setOrgLink(String orgLink) {
        this.orgLink = orgLink;
    }

    public String getMainImg() {
        return mainImg;
    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getUseTarget() {
        return useTarget;
    }

    public void setUseTarget(String useTarget) {
        this.useTarget = useTarget;
    }

    public String getUseFee() {
        return useFee;
    }

    public void setUseFee(String useFee) {
        this.useFee = useFee;
    }

    public String getInquery() {
        return inquery;
    }

    public void setInquery(String inquery) {
        this.inquery = inquery;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGcode() {
        return gcode;
    }

    public void setGcode(String gcode) {
        this.gcode = gcode;
    }

    public EventBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cultureCode);
        dest.writeString(this.sponsor);
        dest.writeString(this.subCode);
        dest.writeString(this.codeName);
        dest.writeString(this.title);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeString(this.time);
        dest.writeString(this.place);
        dest.writeString(this.orgLink);
        dest.writeString(this.mainImg);
        dest.writeString(this.homepage);
        dest.writeString(this.useTarget);
        dest.writeString(this.useFee);
        dest.writeString(this.inquery);
        dest.writeString(this.program);
        dest.writeString(this.content);
        dest.writeString(this.gcode);
    }

    protected EventBean(Parcel in) {
        this.cultureCode = in.readString();
        this.sponsor = in.readString();
        this.subCode = in.readString();
        this.codeName = in.readString();
        this.title = in.readString();
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.time = in.readString();
        this.place = in.readString();
        this.orgLink = in.readString();
        this.mainImg = in.readString();
        this.homepage = in.readString();
        this.useTarget = in.readString();
        this.useFee = in.readString();
        this.inquery = in.readString();
        this.program = in.readString();
        this.content = in.readString();
        this.gcode = in.readString();
    }

    public static final Creator<EventBean> CREATOR = new Creator<EventBean>() {
        @Override
        public EventBean createFromParcel(Parcel source) {
            return new EventBean(source);
        }

        @Override
        public EventBean[] newArray(int size) {
            return new EventBean[size];
        }
    };
}
