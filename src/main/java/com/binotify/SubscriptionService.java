package com.binotify;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import java.sql.SQLException;

@WebService
public interface SubscriptionService {

    @WebMethod
    void acceptRequest(Integer creatorId, Integer subscriberId) throws SQLException;

    @WebMethod
    void rejectRequest(Integer creatorId, Integer subscriberId) throws SQLException;

    @WebMethod
    Subscription[] getPendingSubscription(Integer page) throws SQLException;

    @WebMethod
    String getStatus(Integer creatorId, Integer subscriberId) throws SQLException;

    @WebMethod
    void addNewSubscription(Subscription subscription) throws  SQLException;
}
