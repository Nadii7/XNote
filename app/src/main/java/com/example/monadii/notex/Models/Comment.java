package com.example.monadii.notex.Models;

public class Comment {
    private String cId, comment, timestamp, uId, uMail, uName, uAvatar;

    public Comment() {
    }

    public Comment(String cId, String comment, String timestamp, String uId, String uMail, String uName, String uAvatar) {
        this.cId = cId;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uId = uId;
        this.uMail = uMail;
        this.uName = uName;
        this.uAvatar = uAvatar;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuMail() {
        return uMail;
    }

    public void setuMail(String uMail) {
        this.uMail = uMail;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuAvatar() {
        return uAvatar;
    }

    public void setuAvatar(String uAvatar) {
        this.uAvatar = uAvatar;
    }
}
