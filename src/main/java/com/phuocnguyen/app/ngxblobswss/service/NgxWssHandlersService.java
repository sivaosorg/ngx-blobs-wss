package com.phuocnguyen.app.ngxblobswss.service;

import com.ngxsivaos.model.request.MessagesSocketPublisherRequest;
import com.ngxsivaos.utilities.JsonUtility;
import com.ngxsivaos.utils.ViolationUtils;
import com.phuocnguyen.app.ngxblobswss.config.NgxWssClustersConfig;
import com.sivaos.Utility.CollectionsUtility;
import com.sivaos.Utility.StringUtility;
import com.sivaos.Utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings({
        "FieldCanBeLocal",
        "DuplicatedCode"
})
@Service(value = "ngxWssHandlersService")
public class NgxWssHandlersService implements NgxAppIdHandlersBaseService {

    private static final Logger logger = LoggerFactory.getLogger(NgxWssHandlersService.class);

    private String appId;

    @Override
    public String getAppId() {
        return this.appId;
    }

    @Override
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public void publishEvent(String message, Session _session) {

        if (StringUtility.isEmpty(getAppId())) {
            if (logger.isErrorEnabled()) {
                logger.error("(wss). func 'publishEvent', app_id must be specified");
            }
            return;
        }

        NgxWssClustersConfig cluster = new NgxWssClustersConfig();
        Set<NgxAppIdHandlersBaseService> handlers = new HashSet<>();

        handlers.add(new NgxWssHandlersService());
        cluster.setAppIdHandlers(handlers);

        LinkedHashSet<Session> sessions = cluster.getSessions(getAppId());

        if (CollectionsUtility.isNotEmpty(sessions)) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    cluster.publishEvent(session, message);
                }
            }
        }
    }

    /**
     * @param request       - {@link MessagesSocketPublisherRequest}
     * @param fieldsIgnored - list of fields ignored when publishing messages
     */
    private void validatePayload(MessagesSocketPublisherRequest<?> request, String... fieldsIgnored) {
        if (!ObjectUtils.allNotNull(request)) {
            if (logger.isErrorEnabled()) {
                logger.error("wss::args is required");
            }
            return;
        }

        if (StringUtility.isEmpty(request.getTopic())) {
            if (logger.isErrorEnabled()) {
                logger.error("wss::topic is required");
            }
            return;
        }

        if (!ObjectUtils.allNotNull(request.getMessage())) {
            if (logger.isErrorEnabled()) {
                logger.error("wss::message is required");
            }

            throw new IllegalArgumentException("wss::message is required");
        }
    }

    @Override
    public void publishEvent(@Valid MessagesSocketPublisherRequest<?> message, String... fieldsIgnored) {
        ViolationUtils.isConfirmed(message);
        validatePayload(message, fieldsIgnored);
        publishEvent(JsonUtility.toJsonFieldsIgnored(message, fieldsIgnored), null);
    }
}
