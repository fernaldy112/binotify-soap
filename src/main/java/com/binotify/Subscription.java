package com.binotify;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Subscription {

    private int creatorId;
    private int subscriberId;
    private SubscriptionStatus status;

    public Subscription(int creatorId, int subscriberId, SubscriptionStatus status) {
        this.creatorId = creatorId;
        this.subscriberId = subscriberId;
        this.status = status;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public static Subscription[] castToSubscription(ResultSet rs) throws SQLException {
        ArrayList<Subscription> subscriptions = new ArrayList<>();
        while (rs.next()) {
            int creatorId = rs.getInt("creator_id");
            int subscriberId = rs.getInt("subscriber_id");
            SubscriptionStatus status = SubscriptionStatus.PENDING;
            Subscription subscription = new Subscription(creatorId, subscriberId, status);
            subscriptions.add(subscription);
        }

        Object[] arr = subscriptions.toArray();
        Subscription[] subsArr = new Subscription[arr.length];

        for (int i=0; i<arr.length; i++){
            subsArr[i] = (Subscription) arr[i];
        }

        return subsArr;
    }
}
