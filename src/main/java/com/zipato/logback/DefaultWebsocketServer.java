package com.zipato.logback;

import java.io.IOException;

public class DefaultWebsocketServer extends NanoWSDServer<LoggingWebSocket> {

    public DefaultWebsocketServer(int port) throws IOException {
        super(port);
    }

    @Override
    public LoggingWebSocket createWebsocket(IHTTPSession handshake) {
        LoggingWebSocket webSocket = new LoggingWebSocket(this, handshake);
        return webSocket;
    }
}
