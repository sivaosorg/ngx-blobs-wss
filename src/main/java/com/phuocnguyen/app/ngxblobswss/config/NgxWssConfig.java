package com.phuocnguyen.app.ngxblobswss.config;

import com.ngxsivaos.model.properties.TunnelSocketProperties;
import com.phuocnguyen.app.ngxblobssrv.service.NgxTunnelSocketBaseService;
import com.phuocnguyen.app.ngxblobssrv.service.NgxWebsocketBaseService;
import com.phuocnguyen.app.ngxblobssrv.service.serviceImpl.NgxTunnelSocketBaseServiceImpl;
import com.phuocnguyen.app.ngxblobssrv.service.serviceImpl.NgxWebsocketBaseServiceImpl;
import com.phuocnguyen.app.ngxblobswss.model.interceptor.SessionHandshakeInterceptor;
import com.phuocnguyen.app.ngxblobswss.service.NgxAppIdHandlersBaseService;
import com.phuocnguyen.app.ngxblobswss.service.impl.NgxAppIdHandlersBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/*
implementation group: 'org.springframework.boot', name: 'spring-boot-starter-websocket', version: '2.4.5'
*/

@SuppressWarnings({"All"})
@Configuration
@EnableWebSocket
public class NgxWssConfig implements WebSocketConfigurer {

    @Autowired
    private TunnelSocketProperties tunnelSocketProperties;

    @Autowired
    private NgxTunnelSocketBaseService ngxTunnelSocketBaseService;

    @Bean
    public TunnelSocketProperties tunnelSocketProperties() {
        return new TunnelSocketProperties();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(1024000); // 1MB
        return container;
    }

    @Bean
    public NgxWebsocketBaseService ngxWebsocketBaseService() {
        return new NgxWebsocketBaseServiceImpl();
    }

    @Bean
    public NgxTunnelSocketBaseService ngxTunnelSocketBaseService() {
        return new NgxTunnelSocketBaseServiceImpl();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        HandshakeInterceptor[] handshakeInterceptors = new HandshakeInterceptor[]{
                new SessionHandshakeInterceptor()
        };
        ngxTunnelSocketBaseService.registerTunnelsSocketHandlers(registry, handshakeInterceptors, ngxWebsocketBaseService(), tunnelSocketProperties);
    }

    /**
     * @description If you are using the spring boot built-in web container tomcat, you need to add this class, otherwise an error will be reported
     * If you use an external web container, you don’t need to add it
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
    
    @Bean
    public NgxAppIdHandlersBaseService ngxAppIdHandlersBaseService() {
        return new NgxAppIdHandlersBaseServiceImpl(tunnelSocketProperties);
    }
}
