package com.zipato.logback;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.IOException;

class LoggingWebSocket extends NanoWSD.WebSocket implements ClientWebsocket {

    private final NanoWSD server;
    private final LoggingWebsocketClient client;

    public LoggingWebSocket(NanoWSD server, NanoHTTPD.IHTTPSession handshakeRequest) {
        super(handshakeRequest);
        this.server = server;
        this.client = new LoggingWebsocketClient(this, handshakeRequest.getRemoteIpAddress() + ':' + handshakeRequest.getUri());
    }

    @Override
    protected void onOpen() {
    }

    @Override
    protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
        client.close();
    }

    @Override
    protected void onMessage(NanoWSD.WebSocketFrame message) {
    }

    @Override
    protected void onPong(NanoWSD.WebSocketFrame pong) {
    }

    @Override
    protected void onException(IOException exception) {
    }

    @Override
    protected void debugFrameReceived(NanoWSD.WebSocketFrame frame) {
    }

    @Override
    protected void debugFrameSent(NanoWSD.WebSocketFrame frame) {
    }

    @Override
    public WebsocketReceiverClient getClient() {
        return client;
    }
}
