package com.phuocnguyen.app.ngxblobswss.config;

import com.ngxsivaos.model.request.MessagesSocketPublisherRequest;
import com.ngxsivaos.utilities.JsonUtility;
import com.phuocnguyen.app.ngxblobswss.service.NgxAppIdHandlersBaseService;
import com.sivaos.Utility.CollectionsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/endpoint/{appId}")
public class NgxWssClustersConfig {
    private static final Logger logger = LoggerFactory.getLogger(NgxWssClustersConfig.class);

    // must be static synchronized
    private static final ConcurrentHashMap<String, LinkedHashSet<Session>> APP_ID_CONNECTED_SESSIONS = new ConcurrentHashMap<>();
    // must be static synchronized
    private static Set<NgxAppIdHandlersBaseService> handlers;

    @OnOpen
    public void _onOpen(Session session, @PathParam(value = "appId") String appId) {
        LinkedHashSet<Session> sessions = getSessions(appId);

        if (CollectionsUtility.isEmpty(sessions)) {
            sessions = new LinkedHashSet<>();
            APP_ID_CONNECTED_SESSIONS.put(appId, sessions);
        }

        sessions.add(session);
        if (logger.isInfoEnabled()) {
            logger.info("(on_open). app_id '{}' registered successfully, size of sessions {}", appId, sessions.size());
        }
    }

    @OnClose
    public void _onClose(Session session, @PathParam(value = "appId") String appId) {
        LinkedHashSet<Session> sessions = getSessions(appId);
        if (CollectionsUtility.isNotEmpty(sessions)) {
            sessions.remove(session);
            if (logger.isInfoEnabled()) {
                logger.info("(on_close). app_id '{}' disconnected successfully, size of sessions {}", appId, sessions.size());
            }
        }
    }

    @OnMessage
    public void _onMessage(String message, Session session, @PathParam(value = "appId") String appId) {
        for (NgxAppIdHandlersBaseService handler : handlers) {
            if (Objects.equals(handler.getAppId(), appId)) {
                handler.publishEvent(message, session);
            }
        }
    }

    @OnError
    public void _onError(Session session, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(e.getMessage(), e);
            logger.error("(on_error). wss session_id = {} has an error occurred: {}", session.getId(), e.getMessage());
        }
    }

    /**
     * @param appId -
     */
    public LinkedHashSet<Session> getSessions(String appId) {
        return APP_ID_CONNECTED_SESSIONS.get(appId);
    }

    /**
     * @param session - {@link Session}
     * @param message -
     */
    public void publishEvent(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param session - {@link Session}
     * @param message -
     */
    public void publishEvent(Session session, MessagesSocketPublisherRequest<?> message, String... fieldsIgnored) {
        try {
            session.getBasicRemote().sendText(JsonUtility.toJsonFieldsIgnored(message, fieldsIgnored));
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param handlers - {@link NgxAppIdHandlersBaseService}
     */
    public void setAppIdHandlers(Set<NgxAppIdHandlersBaseService> handlers) {
        NgxWssClustersConfig.handlers = handlers;
    }
}
