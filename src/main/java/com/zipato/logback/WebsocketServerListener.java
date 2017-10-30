package com.zipato.logback;

import ch.qos.logback.core.net.server.ServerListener;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WebsocketServerListener implements ServerListener<WebsocketReceiverClient> {

    private final WebsocketServer server;
    private final String path;
    private final String html;
    private final BlockingQueue<WebsocketReceiverClient> queue;

    public WebsocketServerListener(WebsocketServer server, String path, String html, int backlog) {
        this.path = path;
        this.html = html;
        this.server = server;
        this.queue = new LinkedBlockingQueue<WebsocketReceiverClient>(backlog);
        server.addListener(path, this);
    }

    public boolean offer(WebsocketReceiverClient client) {
        return queue.offer(client);
    }

    @Override
    public WebsocketReceiverClient acceptClient() throws IOException, InterruptedException {
        return queue.take();
    }

    @Override
    public void close() {
        server.removeListener(path);
        server.close();
    }

    public WebsocketServer getServer() {
        return server;
    }

    public String getPath() {
        return path;
    }

    public String getHtml() {
        return html;
    }
}
