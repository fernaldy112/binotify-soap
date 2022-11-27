package com.binotify;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.spi.http.HttpExchange;

import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.Properties;

import static com.binotify.Env.ENV;

// TODO: add meaningful log description
@WebService
public class SubscriptionServiceImpl implements SubscriptionService {

    @Resource
    WebServiceContext context;

    @WebMethod
    public void acceptRequest(int creatorId, int subscriberId) throws SQLException {
        this.updateSubscriptionStatus(creatorId, subscriberId, SubscriptionStatus.ACCEPTED);

        SubscriptionServiceImpl.log(
                this.getClientIpAddress(),
                "subscription#acceptRequest",
                "blah",
                new Timestamp(System.currentTimeMillis()).toString()
        );
    }

    @WebMethod
    public void rejectRequest(int creatorId, int subscriberId) throws SQLException {
        this.updateSubscriptionStatus(creatorId, subscriberId, SubscriptionStatus.REJECTED);

        SubscriptionServiceImpl.log(
                this.getClientIpAddress(),
                "subscription#rejectRequest",
                "blah",
                new Timestamp(System.currentTimeMillis()).toString()
        );
    }

    @WebMethod
    public String getStatus(int creatorId, int subscriberId) throws SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        ResultSet res = statement.executeQuery("SELECT * FROM subscription WHERE creator_id = "
                + creatorId + " AND subscriber_id = " + subscriberId + ";");
        Subscription[] subscriptions = Subscription.castToSubscription(res);

        SubscriptionServiceImpl.log(
                this.getClientIpAddress(),
                "subscription#getStatus",
                "blah",
                new Timestamp(System.currentTimeMillis()).toString()
        );

        return subscriptions.length > 0
                ? subscriptions[0].getStatus().toString()
                : null;
    }

    @Override
    public Subscription[] getPendingSubscription(int page) throws SQLException {
        Properties props = new Properties();
        props.put("user", ENV.get("DB_USER"));
        props.put("password", ENV.get("DB_PASS"));
        String url = String.format("jdbc:mysql://%s:%s/%s",
                ENV.get("DB_HOST"),
                ENV.get("DB_PORT"),
                ENV.get("DB_NAME"));
        Connection connection = DriverManager.getConnection(url, props);

        Statement statement = connection.createStatement();
        int offset = (page - 1) * 20;
        ResultSet rs = statement
                .executeQuery("SELECT * FROM subscription WHERE status = 'pending' LIMIT 21 OFFSET " + offset);

        Subscription[] subsArr = Subscription.castToSubscription(rs);

        SubscriptionServiceImpl.log(
                this.getClientIpAddress(),
                "subscription#getPendingSubscription",
                "blah",
                new Timestamp(System.currentTimeMillis()).toString()
        );

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
                ENV.get("DB_NAME"));
        Connection connection = DriverManager.getConnection(url, props);

        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO subscription VALUES ("
                + subscription.getCreatorId() + ", "
                + subscription.getSubscriberId() + ", '"
                + subscription.getStatus().toString() + "')");

        SubscriptionServiceImpl.log(
                this.getClientIpAddress(),
                "subscription#addNewSubscription",
                "blah",
                new Timestamp(System.currentTimeMillis()).toString()
        );

    }

    private String getClientIpAddress() {
        MessageContext messageContext = context.getMessageContext();
        HttpExchange exchange = (HttpExchange) messageContext.get("com.sun.xml.ws.http.exchange");
        InetAddress address = exchange.getRemoteAddress().getAddress();
        return address.toString();
    }

    private void updateSubscriptionStatus(
            int creatorId,
            int subscriberId,
            SubscriptionStatus status) throws SQLException {
        Connection connection = getConnection();

        Statement statement = connection.createStatement();
        statement.executeUpdate("UPDATE subscription SET status = '"
                + status.toString() + "' WHERE creator_id = "
                + creatorId + " AND subscriber_id = " + subscriberId);

        String payload = String.format("{\"creatorId\": %d, \"subscriberId\": %d, \"status\": \"%s\"}",
                creatorId,
                subscriberId,
                status
        );

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENV.get("APP_URL") + "/subscribe"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) { }
    }

    private static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.put("user", ENV.get("DB_USER"));
        props.put("password", ENV.get("DB_PASS"));
        String url = String.format("jdbc:mysql://%s:%s/%s",
                ENV.get("DB_HOST"),
                ENV.get("DB_PORT"),
                ENV.get("DB_NAME"));
        return DriverManager.getConnection(url, props);
    }

    private static void log(String ipAddress, String endpoint, String description, String timestamp) throws
            SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();


        statement.executeQuery(
                String.format(
                        "INSERT INTO logging (description, IP, endpoint, requested_at) VALUES (%s, %s, %s, %s)",
                        description,
                        ipAddress,
                        endpoint,
                        timestamp
                )
                );
    }

}
