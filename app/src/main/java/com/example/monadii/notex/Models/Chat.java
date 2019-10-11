package com.example.monadii.notex.Models;

public class Chat {
    private String message , sender , receiver , timestamp , messagePic ;
    private boolean status;
    private int status_icon;

    public Chat() {
    }


    public Chat(String message, String sender, String receiver, String timestamp, String messagePic, boolean status, int status_icon) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.messagePic = messagePic;
        this.status = status;
        this.status_icon = status_icon;
    }

    public String getMessagePic() {
        return messagePic;
    }

    public void setMessagePic(String messagePic) {
        this.messagePic = messagePic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getStatus_icon() {
        return status_icon;
    }

    public void setStatus_icon(int status_icon) {
        this.status_icon = status_icon;
    }
}
