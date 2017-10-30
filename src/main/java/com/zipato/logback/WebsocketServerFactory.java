package com.zipato.logback;

import java.io.IOException;

public interface WebsocketServerFactory {

    WebsocketServer createServer(WebsocketAppenderBase<?> appender) throws IOException;

}
