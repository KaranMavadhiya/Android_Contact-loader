package com.contact.pojo;

public class Contact{

    private String _id;
    private String contactId;
    private String displayName;
    private String number;
    private String emailAddress;
    private String userThumbnail;

    private int color = -1;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUserThumbnail() {
        return userThumbnail;
    }

    public void setUserThumbnail(String userThumbnail) {
        this.userThumbnail = userThumbnail;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}