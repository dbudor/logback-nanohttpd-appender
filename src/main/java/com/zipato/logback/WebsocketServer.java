package com.zipato.logback;

public interface WebsocketServer<C extends ClientWebsocket> {

    void addListener(String path, WebsocketServerListener listener);

    void removeListener(String path);

    void close();

}
