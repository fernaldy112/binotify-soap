package com.binotify;


import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
        statement.executeQuery("UPDATE subscription SET status = '"
                + status.toString() + "' WHERE" + "creator_id = "
                + creatorId + " AND subscriber_id = " + subscriberId);
    }

}

