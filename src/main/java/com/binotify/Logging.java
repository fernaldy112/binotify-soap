package com.binotify;

public class Logging {

    private int id;
    private String description;
    private String IP;
    private String endpoint;
    private String requested_at;

    public Logging(int id, String description, String IP, String endpoint, String requested_at) {
        this.id = id;
        this.description = description;
        this.IP = IP;
        this.endpoint = endpoint;
        this.requested_at = requested_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRequested_at() {
        return requested_at;
    }

    public void setRequested_at(String requested_at) {
        this.requested_at = requested_at;
    }
}
