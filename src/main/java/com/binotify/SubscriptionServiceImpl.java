package com.binotify;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.binotify.Env.ENV;

@WebService
public class SubscriptionServiceImpl implements SubscriptionService {

    @Resource
    WebServiceContext serviceContext;

    private Context context;

    private void wrapContext() {
        this.context = new Context(this.serviceContext);
    }

    private boolean validateApiKey() {
        this.wrapContext();

        boolean valid = KeyProvider.validate(this.context.getClientApiKey());
        if (!valid) {
            this.context.invalidate();
        }

        return valid;
    }

    @WebMethod
    public void acceptRequest(Integer creatorId, Integer subscriberId) throws SQLException {
        if (!this.validateApiKey()) {
            return;
        }

        this.updateSubscriptionStatus(creatorId, subscriberId, SubscriptionStatus.ACCEPTED);

        List<String> args = new ArrayList<>();
        args.add(creatorId.toString());
        args.add(subscriberId.toString());
        SubscriptionServiceImpl.log(
                this.context.getClientIpAddress(),
                "acceptRequest",
                args,
                new Timestamp(System.currentTimeMillis()).toString()
        );
    }

    @WebMethod
    public void rejectRequest(Integer creatorId, Integer subscriberId) throws SQLException {
        if (!this.validateApiKey()) {
            return;
        }

        this.updateSubscriptionStatus(creatorId, subscriberId, SubscriptionStatus.REJECTED);

        List<String> args = new ArrayList<>();
        args.add(creatorId.toString());
        args.add(subscriberId.toString());
        SubscriptionServiceImpl.log(
                this.context.getClientIpAddress(),
                "rejectRequest",
                args,
                new Timestamp(System.currentTimeMillis()).toString()
        );
    }

    @WebMethod
    public String getStatus(Integer creatorId, Integer subscriberId) throws SQLException {
        if (!this.validateApiKey()) {
            return null;
        }

        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        ResultSet res = statement.executeQuery("SELECT * FROM subscription WHERE creator_id = "
                + creatorId + " AND subscriber_id = " + subscriberId + ";");
        Subscription[] subscriptions = Subscription.castToSubscription(res);

        List<String> args = new ArrayList<>();
        args.add(creatorId.toString());
        args.add(subscriberId.toString());
        SubscriptionServiceImpl.log(
                this.context.getClientIpAddress(),
                "getStatus",
                args,
                new Timestamp(System.currentTimeMillis()).toString()
        );

        return subscriptions.length > 0
                ? subscriptions[0].getStatus().toString()
                : null;
    }

    @Override
    public Subscription[] getPendingSubscription(Integer page) throws SQLException {
        if (!this.validateApiKey()) {
            return null;
        }

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

        List<String> args = new ArrayList<>();
        args.add(page.toString());
        SubscriptionServiceImpl.log(
                this.context.getClientIpAddress(),
                "getPendingSubscription",
                args,
                new Timestamp(System.currentTimeMillis()).toString()
        );

        return subsArr;
    }

    @Override
    public void addNewSubscription(Subscription subscription) throws SQLException {
        if (!this.validateApiKey()) {
            return;
        }

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
                this.context.getClientIpAddress(),
                "addNewSubscription",
                new ArrayList<>(),
                new Timestamp(System.currentTimeMillis()).toString()
        );

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

    private static void log(String ipAddress, String method, List<String> args, String timestamp) throws
            SQLException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        StringBuilder argString = new StringBuilder(method).append("(");
        int size = args.size();
        for (int i = 0; i < size; i++) {
            String arg = args.get(i);
            if (i != 0) argString.append(", ");
            argString.append(arg);
        }
        argString.append(")");

        String endpoint = "subscription#" + method;
        String description = ipAddress + " called " + argString + " at " + timestamp + ".";
        String query = String.format(
                "INSERT INTO logging (description, IP, endpoint, requested_at) VALUES ('%s', '%s', '%s', '%s')",
                description,
                ipAddress,
                endpoint,
                timestamp
        );

        statement.executeUpdate(query);
    }

}
