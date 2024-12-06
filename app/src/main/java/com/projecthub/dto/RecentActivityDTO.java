package com.projecthub.dto;

public class RecentActivityDTO {
    private String timestamp;
    private String activity;
    private String user;

    public RecentActivityDTO(String timestamp, String activity, String user) {
        this.timestamp = timestamp;
        this.activity = activity;
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}