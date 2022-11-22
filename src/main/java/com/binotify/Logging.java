package com.binotify;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import java.sql.*;

public class Logging {

    private int id;
    private String description;
    private String IP;
    private String endpoint;
    private String requested_at;

    public Logging(int id){
        this.id = id;
        this.description = "gatau"; //API KEY Belum tau caranya
        HttpExchange exchange = (HttpExchange)msgx.get("com.sun.xml.ws.http.exchange");
        InetSocketAddress remoteAddress = exchange.getRemoteAddress();
        String remoteHost = remoteAddress.getHostName();
        this.IP = remoteHost;
        this.endpoint = "gatau"; //Belum tau caranya
        Timestamp timestamp = new Timestamp(System.currentTimeMillis())
        this.requested_at = timestamp;
        ResultSet rs = statement.executeQuery("INSERT INTO logging (id, description, IP, endpoint, requested_at) VALUES (this.id, this.description, this.IP, this.endpoint, this.requested_at)");
    }

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
