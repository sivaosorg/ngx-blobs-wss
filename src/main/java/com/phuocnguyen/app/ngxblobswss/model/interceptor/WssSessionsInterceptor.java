package com.phuocnguyen.app.ngxblobswss.model.interceptor;

import com.phuocnguyen.app.ngxblobswss.model.context.WebSocketRequestDataContext;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;

public class WssSessionsInterceptor extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig endpoint, HandshakeRequest request, HandshakeResponse response) {
        // HttpSession session = (HttpSession) request.getHttpSession();
        // endpoint.getUserProperties().put(HttpSession.class.getName(), session);

        Map<String, List<String>> headers = request.getHeaders();
        String remoteAddr = (String) ((HttpSession) request.getHttpSession()).getAttribute("remoteAddr");

        // We don't use config.getUserProperties.add because it isn't always one-to-one with a web socket connection; we use ThreadLocal instead
        WebSocketRequestDataContext.setCurrentInstance(new WebSocketRequestDataContext(headers, remoteAddr));
    }
}
