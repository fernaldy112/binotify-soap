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

    public static SubscriptionStatus from(String status){
        return switch (status){
            case "ACCEPTED" -> SubscriptionStatus.ACCEPTED;
            case "REJECTED" -> SubscriptionStatus.REJECTED;
            case "PENDING" -> SubscriptionStatus.PENDING;
            default -> null;
        };
    }
}