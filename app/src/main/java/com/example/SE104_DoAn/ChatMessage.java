package com.example.SE104_DoAn;

import java.util.Date;

public class ChatMessage {
    private String userId;
    private String username;
    private String message;
    private long timestamp;

    public ChatMessage() {
        // Constructor mặc định cần thiết cho Firebase
    }

    public ChatMessage(String userId, String username, String message, long timestamp) {
        this.userId = userId;
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}