package com.phuocnguyen.app.ngxblobswss.service;

import com.ngxsivaos.model.request.MessagesSocketPublisherRequest;

import javax.validation.Valid;
import javax.websocket.Session;

public interface NgxAppIdHandlersBaseService {

    String getAppId();

    void setAppId(String appId);

    void publishEvent(String message, Session session);

    void publishEvent(@Valid MessagesSocketPublisherRequest<?> message);

    void publishEvent(@Valid MessagesSocketPublisherRequest<?> message, String... fieldsIgnored);

    void publishEvent(String appId, @Valid MessagesSocketPublisherRequest<?> message, String... fieldsIgnored);
}
