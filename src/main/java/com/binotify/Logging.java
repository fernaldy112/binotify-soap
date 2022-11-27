// ! TODO: remove

package com.binotify;

import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.spi.http.HttpExchange;

import java.net.InetSocketAddress;
import java.sql.*;

public class Logging {

    @Resource
    WebServiceContext context;

    private int id;
    private String description;
    private String IP;
    private String endpoint;
    private String requestedAt;

    public Logging(String description, String endpoint) {
//        this.description = description;
//        MessageContext messageContext = context.getMessageContext();
//        HttpExchange exchange = (HttpExchange) messageContext.get("com.sun.xml.ws.http.exchange");
//        InetSocketAddress remoteAddress = exchange.getRemoteAddress();
//        String remoteHost = remoteAddress.getHostName();
//        this.IP = remoteHost;
//        this.endpoint = endpoint;
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        this.requestedAt = timestamp.toString();
//
//
//        ResultSet rs = statement.executeQuery(
//                "INSERT INTO logging (description, IP, endpoint, requested_at) VALUES (this.description, this.IP, this.endpoint, this.requested_at)");
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

    public String getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }
}
