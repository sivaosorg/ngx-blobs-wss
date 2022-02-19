package com.phuocnguyen.app.ngxblobswss.config;

import com.ngxsivaos.model.properties.TunnelSocketProperties;
import com.phuocnguyen.app.ngxblobssrv.service.NgxTunnelSocketBaseService;
import com.phuocnguyen.app.ngxblobssrv.service.NgxWebsocketBaseService;
import com.phuocnguyen.app.ngxblobssrv.service.serviceImpl.NgxTunnelSocketBaseServiceImpl;
import com.phuocnguyen.app.ngxblobssrv.service.serviceImpl.NgxWebsocketBaseServiceImpl;
import com.phuocnguyen.app.ngxblobswss.model.interceptor.NgxHttpSessionHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/*
implementation group: 'org.springframework.boot', name: 'spring-boot-starter-websocket', version: '2.4.5'
*/

@SuppressWarnings({"All"})
@Configuration
@EnableWebSocket
public class NgxWebSocketConfig implements WebSocketConfigurer {

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
        container.setMaxBinaryMessageBufferSize(1024000);
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
                new NgxHttpSessionHandshakeInterceptor()
        };
        ngxTunnelSocketBaseService.registerTunnelsSocketHandlers(registry, handshakeInterceptors, ngxWebsocketBaseService(), tunnelSocketProperties);
    }
}
