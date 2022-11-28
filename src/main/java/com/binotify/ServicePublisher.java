package com.binotify;

import jakarta.xml.ws.Endpoint;

public class ServicePublisher {
    public static void main(String[] args) {
        KeyProvider.generate();

        Endpoint.publish(
                "http://0.0.0.0:8086/subscription",
                new SubscriptionServiceImpl());
    }
}
