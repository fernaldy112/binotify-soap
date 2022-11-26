package com.binotify;

import jakarta.xml.ws.Endpoint;

public class ServicePublisher {
    public static void main(String[] args) {
        Endpoint.publish(
                "http://localhost:80/subscription",
                new SubscriptionServiceImpl());
    }
}
