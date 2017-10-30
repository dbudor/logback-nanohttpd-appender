package com.zipato.logback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DefaultWebsocketServerFactory implements WebsocketServerFactory {

    private static DefaultWebsocketServerFactory instance = new DefaultWebsocketServerFactory();

    private Map<Integer, NanoWSDServer> serverMap = new HashMap<Integer, NanoWSDServer>(1);

    @Override
    public WebsocketServer createServer(WebsocketAppenderBase<?> appender) throws IOException {

        int port = appender.getPort();
        NanoWSDServer ws = instance.serverMap.get(port);
        if (ws == null) {
            ws = new DefaultWebsocketServer(port);
            instance.serverMap.put(port, ws);
        }
        return ws;
    }
}
