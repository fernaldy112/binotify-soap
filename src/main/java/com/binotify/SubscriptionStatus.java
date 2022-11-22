package com.binotify;

public enum SubscriptionStatus {
    ACCEPTED,
    REJECTED,
    PENDING;

    public String toString() {
        return switch (this) {
            case ACCEPTED -> "ACCEPTED";
            case REJECTED -> "REJECTED";
            case PENDING -> "PENDING";
        };
    }
}