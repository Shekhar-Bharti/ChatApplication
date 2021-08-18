package com.example.chatapplication.Models;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MessageModel {
    String senderId;
    String message;
    Boolean seen;
    private long timeStamp;

    public MessageModel()
    {

    }
    public MessageModel(String senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }

    public MessageModel(String senderId, String message, long timeStamp) {
        this.senderId = senderId;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public MessageModel(String senderId, String message, Boolean seen, long timeStamp) {
        this.senderId = senderId;
        this.message = message;
        this.seen = seen;
        this.timeStamp = timeStamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
