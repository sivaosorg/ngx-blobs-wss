package com.phuocnguyen.app.ngxblobswss.service;

import com.ngxsivaos.model.request.MessagesSocketPublisherRequest;

import javax.validation.Valid;
import javax.websocket.Session;

public interface NgxAppIdHandlersBaseService {

    int DEFAULT_POOL_SIZE_THREAD = 10;

    void setPoolSizeThreads(int poolSizeThreads);

    String getAppId();

    void setAppId(String appId);

    void publishEvent(String message, Session session);

    void publishEvent(@Valid MessagesSocketPublisherRequest<?> message);

    void publishEvent(@Valid MessagesSocketPublisherRequest<?> message, String... fieldsIgnored);

    void publishEvent(String appId, @Valid MessagesSocketPublisherRequest<?> message, String... fieldsIgnored);
}
