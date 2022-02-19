package com.phuocnguyen.app.ngxblobswss.model.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

@SuppressWarnings({"FieldMayBeFinal"})
public class NgxHttpSessionHandshakeInterceptor implements HandshakeInterceptor {

    public static final String HTTP_SESSION_ID_ATTR_NAME = "HTTP.SESSION.ID";
    private static final Logger logger = LoggerFactory.getLogger(NgxHttpSessionHandshakeInterceptor.class);
    private Collection<String> attributeNames;

    private boolean copyAllAttributes;

    private boolean copyHttpSessionId = true;

    private boolean createSession;

    public NgxHttpSessionHandshakeInterceptor() {
        this.attributeNames = Collections.emptyList();
        this.copyAllAttributes = true;
    }

    public NgxHttpSessionHandshakeInterceptor(Collection<String> attributeNames) {
        this.attributeNames = Collections.unmodifiableCollection(attributeNames);
        this.copyAllAttributes = false;
    }

    /**
     * Return the configured attribute names to copy (read-only).
     */
    public Collection<String> getAttributeNames() {
        return this.attributeNames;
    }

    /**
     * Whether to copy all HTTP session attributes.
     */
    public boolean isCopyAllAttributes() {
        return this.copyAllAttributes;
    }

    /**
     * Whether to copy all attributes from the HTTP session. If set to "true",
     * any explicitly configured attribute names are ignored.
     * <p>By default this is set to either "true" or "false" depending on which
     * constructor was used (default or with attribute names respectively).
     *
     * @param copyAllAttributes whether to copy all attributes
     */
    public void setCopyAllAttributes(boolean copyAllAttributes) {
        this.copyAllAttributes = copyAllAttributes;
    }

    /**
     * Whether to copy the HTTP session id to the handshake attributes.
     */
    public boolean isCopyHttpSessionId() {
        return this.copyHttpSessionId;
    }

    /**
     * Whether the HTTP session id should be copied to the handshake attributes
     * under the key {@link #HTTP_SESSION_ID_ATTR_NAME}.
     * <p>By default this is "true".
     *
     * @param copyHttpSessionId whether to copy the HTTP session id.
     */
    public void setCopyHttpSessionId(boolean copyHttpSessionId) {
        this.copyHttpSessionId = copyHttpSessionId;
    }

    /**
     * Whether the HTTP session is allowed to be created.
     */
    public boolean isCreateSession() {
        return this.createSession;
    }

    /**
     * Whether to allow the HTTP session to be created while accessing it.
     * <p>By default set to {@code false}.
     *
     * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
     */
    public void setCreateSession(boolean createSession) {
        this.createSession = createSession;
    }

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(isCreateSession());
        }
        return null;
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        attributes.put("ip", request.getRemoteAddress());
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

        logger.info("NgxHttpSessionHandshakeInterceptor::beforeHandshake()::request.getHeaders(): {}", request.getHeaders());

        attributes.forEach((key, value) -> {
            logger.info("NgxHttpSessionHandshakeInterceptor::beforeHandshake()::attributes has Key: {}, Value: {} ", key, value);
        });

        HttpSession session = getSession(request);

        if (session != null) {

            if (isCopyHttpSessionId()) {
                attributes.put(HTTP_SESSION_ID_ATTR_NAME, session.getId());
            }

            Enumeration<String> names = session.getAttributeNames();

            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (isCopyAllAttributes() || getAttributeNames().contains(name)) {
                    attributes.put(name, session.getAttribute(name));
                }
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        logger.info("NgxHttpSessionHandshakeInterceptor::afterHandshake()");
    }
}
