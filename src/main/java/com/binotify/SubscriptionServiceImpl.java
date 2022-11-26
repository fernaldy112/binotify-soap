package com.binotify;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

import java.sql.*;
import java.util.Properties;

import static com.binotify.Env.ENV;

// TODO: test db connection
@WebService
public class SubscriptionServiceImpl implements SubscriptionService {

    @WebMethod
    public void acceptRequest(int creatorId, int subscriberId) throws SQLException {
        this.updateSubscriptionStatus(creatorId, subscriberId, SubscriptionStatus.ACCEPTED);
    }

    @WebMethod
    public void rejectRequest(int creatorId, int subscriberId) throws SQLException {
        this.updateSubscriptionStatus(creatorId, subscriberId, SubscriptionStatus.REJECTED);
    }

    @Override
    public Subscription[] getPendingSubscription(int page) throws SQLException{
        Properties props = new Properties();
        props.put("user", ENV.get("DB_USER"));
        props.put("password", ENV.get("DB_PASS"));
        String url = String.format("jdbc:mysql://%s:%s/%s",
                ENV.get("DB_HOST"),
                ENV.get("DB_PORT"),
                ENV.get("DB_NAME")
        );
        Connection connection = DriverManager.getConnection(url, props);

        Statement statement = connection.createStatement();
        int offset = (page - 1) * 20;
        ResultSet rs = statement.executeQuery("SELECT * FROM subscription WHERE status = 'pending' LIMIT 21 OFFSET " + offset);

        Subscription[] subsArr = Subscription.castToSubscription(rs);

        return subsArr;
    }

    private void updateSubscriptionStatus(
            int creatorId,
            int subscriberId,
            SubscriptionStatus status
    ) throws SQLException {

        Properties props = new Properties();
        props.put("user", ENV.get("DB_USER"));
        props.put("password", ENV.get("DB_PASS"));
        String url = String.format("jdbc:mysql://%s:%s/%s",
                ENV.get("DB_HOST"),
                ENV.get("DB_PORT"),
                ENV.get("DB_NAME")
        );
        Connection connection = DriverManager.getConnection(url, props);

        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE subscription SET status = '"
                + status.toString() + "' WHERE " + "creator_id = "
                + creatorId + " AND subscriber_id = " + subscriberId);
    }

}

