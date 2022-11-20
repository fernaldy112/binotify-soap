package com.binotify;

public enum SubscriptionStatus {
    ACCEPTED,
    REJECTED;

    public String toString() {
        return switch (this) {
            case ACCEPTED -> "ACCEPTED";
            case REJECTED -> "REJECTED";
        };
    }
}