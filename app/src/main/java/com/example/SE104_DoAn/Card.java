package com.example.SE104_DoAn;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Card implements Parcelable {
    private String title;
    private String creator;
    private String description;
    private List<String> members;
    private Date startDate;
    private Date endDate;
    private List<String> attachments;

    public Card(String title) {
        this.title = title;
        this.description = "";
        this.members = new ArrayList<>();
        this.startDate = null;
        this.endDate = null;
        this.attachments = new ArrayList<>();
    }
    public Card() {
        // Constructor mặc định cần thiết cho Firebase
    }

    protected Card(Parcel in) {
        title = in.readString();
        description = in.readString();
        members = in.createStringArrayList();
        long startDateLong = in.readLong();
        startDate = startDateLong == -1 ? null : new Date(startDateLong);
        long endDateLong = in.readLong();
        endDate = endDateLong == -1 ? null : new Date(endDateLong);
        attachments = in.createStringArrayList();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeStringList(members);
        dest.writeLong(startDate != null ? startDate.getTime() : -1);
        dest.writeLong(endDate != null ? endDate.getTime() : -1);
        dest.writeStringList(attachments);
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

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void addMember(String member) {
        this.members.add(member);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(String attachment) {
        this.attachments.add(attachment);
    }

    public String getCreator() { return creator; }

    public void setCreator(String creator) { this.creator = creator; }
}