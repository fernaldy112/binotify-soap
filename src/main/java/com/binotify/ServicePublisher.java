package com.binotify;

import jakarta.xml.ws.Endpoint;

public class ServicePublisher {
    public static void main(String[] args) {
        Endpoint.publish(
                "http://0.0.0.0:80/subscription",
                new SubscriptionServiceImpl());
    }
}
