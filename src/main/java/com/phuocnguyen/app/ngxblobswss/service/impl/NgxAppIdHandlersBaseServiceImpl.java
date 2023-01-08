package com.phuocnguyen.app.ngxblobswss.service.impl;

import com.ngxsivaos.model.properties.TunnelSocketProperties;
import com.ngxsivaos.model.request.MessagesSocketPublisherRequest;
import com.ngxsivaos.utilities.JsonUtility;
import com.ngxsivaos.utils.ViolationUtils;
import com.phuocnguyen.app.ngxblobswss.config.NgxWssClustersConfig;
import com.phuocnguyen.app.ngxblobswss.service.NgxAppIdHandlersBaseService;
import com.sivaos.Utility.CollectionsUtility;
import com.sivaos.Utility.StringUtility;
import com.sivaos.Utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({
        "FieldCanBeLocal",
        "DuplicatedCode"
})
@Service(value = "ngxAppIdHandlersBaseService")
public class NgxAppIdHandlersBaseServiceImpl implements NgxAppIdHandlersBaseService {

    private static final Logger logger = LoggerFactory.getLogger(NgxAppIdHandlersBaseServiceImpl.class);

    private final TunnelSocketProperties properties;

    private ExecutorService pool = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE_THREAD);
    private String appId;

    @Autowired
    public NgxAppIdHandlersBaseServiceImpl(
            TunnelSocketProperties properties) {
        this.properties = properties;
    }

    private boolean hasSkippedLogs() {
        return properties.getConfig().isAllowDisplaySkippedLog();
    }

    @Override
    public void setPoolSizeThreads(int poolSizeThreads) {
        this.pool = Executors.newFixedThreadPool(poolSizeThreads > 0 ? poolSizeThreads : DEFAULT_POOL_SIZE_THREAD);
    }

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

        handlers.add(new NgxAppIdHandlersBaseServiceImpl(properties));
        cluster.setAppIdHandlers(handlers);

        LinkedHashSet<Session> sessions = cluster.getSessions(StringUtility.trimSingleWhitespace(getAppId()));

        this.pool.execute(() -> {
            if (CollectionsUtility.isNotEmpty(sessions)) {
                try {
                    for (Session session : sessions) {
                        if (session.isOpen()) {
                            cluster.publishEvent(session, message);
                        }
                    }
                } catch (Exception e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(e.getMessage(), e);
                        logger.error("func 'publishEvent' has an error occurred: {}, message: {}",
                                e.getMessage(),
                                JsonUtility.toJson(message));
                    }
                }
            }
        });
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

    @Override
    public void publishEvent(String appId, @Valid MessagesSocketPublisherRequest<?> message, String... fieldsIgnored) {
        setAppId(appId);
        publishEvent(message, fieldsIgnored);
    }

    @Override
    public void publishEvent(@Valid MessagesSocketPublisherRequest<?> message) {
        publishEvent(message, new String[]{});
    }
}
