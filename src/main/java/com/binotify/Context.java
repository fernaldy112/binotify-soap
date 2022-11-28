package com.binotify;

import com.sun.net.httpserver.HttpExchange;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.HeaderList;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

import java.net.InetAddress;
import java.util.HashMap;

public class Context {

    private final MessageContext inner;

    public Context(WebServiceContext context) {
        this.inner = context.getMessageContext();
    }

    private HashMap<String, String> getSoapHeaders() {
        HeaderList headers = ((HeaderList) this.inner.get("com.sun.xml.ws.api.message.HeaderList"));

        HashMap<String, String> soapHeaders = new HashMap<>();
        for (Header header: headers) {
            soapHeaders.put(
                    header.getLocalPart(),
                    header.getStringContent()
            );
        }

        return soapHeaders;
    }

    public String getClientApiKey() {
        HashMap<String, String> headers = this.getSoapHeaders();
        String key = headers.get("ApiKey") == null
                ? ""
                : headers.get("ApiKey");
        return key;
    }

    public String getClientIpAddress() {
        HttpExchange exchange = (HttpExchange) this.inner.get("com.sun.xml.ws.http.exchange");
        InetAddress address = exchange.getRemoteAddress().getAddress();
        return address.toString().replace("/", "");
    }
}
