package com.binotify;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import java.sql.SQLException;

@WebService
public interface SubscriptionService {

    @WebMethod
    void acceptRequest(int creatorId, int subscriberId) throws SQLException;

    @WebMethod
    void rejectRequest(int creatorId, int subscriberId) throws SQLException;

    @WebMethod
    Subscription[] getPendingSubscription(int page) throws SQLException;
}
