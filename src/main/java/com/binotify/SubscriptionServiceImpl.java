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

    @WebMethod
    public String getStatus(int creatorId, int subscriberId) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        ResultSet res = statement.executeQuery("SELECT * FROM subscription WHERE creator_id = "
        + creatorId + " AND subscriber_id = " + subscriberId + ";");
        Subscription[] subscriptions = Subscription.castToSubscription(res);

        return subscriptions.length > 0
                ? subscriptions[0].getStatus().toString()
                : null;
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

    @Override
    public void addNewSubscription(Subscription subscription) throws SQLException {
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
        statement.executeUpdate("INSERT INTO subscription VALUES ("
                + Integer.toString(subscription.getCreatorId()) + ", "
                + Integer.toString(subscription.getSubscriberId()) + ", '"
                + subscription.getStatus().toString() + "')"
                );
    }

    private void updateSubscriptionStatus(
            int creatorId,
            int subscriberId,
            SubscriptionStatus status
    ) throws SQLException {
        Connection connection = getConnection();

        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE subscription SET status = '"
                + status.toString() + "' WHERE creator_id = "
                + creatorId + " AND subscriber_id = " + subscriberId);
    }

    private static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.put("user", ENV.get("DB_USER"));
        props.put("password", ENV.get("DB_PASS"));
        String url = String.format("jdbc:mysql://%s:%s/%s",
                ENV.get("DB_HOST"),
                ENV.get("DB_PORT"),
                ENV.get("DB_NAME")
        );
        return DriverManager.getConnection(url, props);
    }

}

