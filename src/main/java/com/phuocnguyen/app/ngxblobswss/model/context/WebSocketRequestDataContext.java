package com.phuocnguyen.app.ngxblobswss.model.context;

import java.util.List;
import java.util.Map;

public class WebSocketRequestDataContext {

    private static final ThreadLocal<WebSocketRequestDataContext> INSTANCE = new ThreadLocal<WebSocketRequestDataContext>() {

        @Override
        protected WebSocketRequestDataContext initialValue() {
            return null;
        }
    };
    private final Map<String, List<String>> headers;
    private final String remoteAddr;

    public WebSocketRequestDataContext(Map<String, List<String>> headers, String remoteAddr) {
        this.headers = headers;
        this.remoteAddr = remoteAddr;
    }

    public static WebSocketRequestDataContext getCurrentInstance() {
        return INSTANCE.get();
    }

    public static void setCurrentInstance(WebSocketRequestDataContext context) {
        if (context == null) {
            INSTANCE.remove();
        } else {
            INSTANCE.set(context);
        }
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }
}
