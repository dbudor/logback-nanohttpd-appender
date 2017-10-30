package com.zipato.logback;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.net.server.ClientVisitor;
import ch.qos.logback.core.net.server.ServerListener;
import ch.qos.logback.core.net.server.ServerRunner;
import ch.qos.logback.core.spi.PreSerializationTransformer;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

public abstract class WebsocketAppenderBase<E> extends AppenderBase<E> {

    /**
     * Default {@link ServerSocket} backlog
     */
    public static final int DEFAULT_BACKLOG = 50;

    /**
     * Default queue size used for each client
     */
    public static final int DEFAULT_CLIENT_QUEUE_SIZE = 100;
    public static final int DEFAULT_PORT = 6666;

    private int backlog = DEFAULT_BACKLOG;
    private int clientQueueSize = DEFAULT_CLIENT_QUEUE_SIZE;
    private int port = DEFAULT_PORT;
    private String path;
    private String html;
    private boolean includeCallerData;

    private WebsocketServerFactory serverFactory = new DefaultWebsocketServerFactory();
    private Serializer serializer;
    private ServerRunner<WebsocketReceiverClient> runner;

    @Override
    public void start() {
        if (isStarted()) {
            return;
        }
        if (serializer == null) {
            addError("no serializer");
            return;
        }
        try {
            final WebsocketServer server = serverFactory.createServer(this);
            ServerListener<WebsocketReceiverClient> listener = createServerListener(server);
            ScheduledExecutorService executor = getContext().getScheduledExecutorService();
            runner = createServerRunner(listener, executor);
            runner.setContext(getContext());
            executor.execute(runner);
        } catch (Exception ex) {
            addError("server startup error: " + ex, ex);
            return;
        }
        super.start();
    }

    @Override
    public void stop() {
        if (!isStarted())
            return;
        try {
            runner.stop();
            super.stop();
        } catch (IOException ex) {
            addError("server shutdown error: " + ex, ex);
        }
    }


    protected ServerListener<WebsocketReceiverClient> createServerListener(WebsocketServer server) {
        return new WebsocketServerListener(server, path, html, backlog);
    }

    protected ServerRunner<WebsocketReceiverClient> createServerRunner(ServerListener<WebsocketReceiverClient> listener, Executor executor) {
        return new WebsocketServerRunner(listener, executor, clientQueueSize, serializer);
    }

    protected abstract void postProcessEvent(E event);

    protected abstract PreSerializationTransformer<E> getPST();

    @Override
    protected void append(E event) {
        if (event == null)
            return;
        postProcessEvent(event);
        final Serializable serEvent = getPST().transform(event);
        runner.accept(new ClientVisitor<WebsocketReceiverClient>() {
            @Override

            public void visit(WebsocketReceiverClient client) {
                client.offer(serEvent);
            }
        });
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getClientQueueSize() {
        return clientQueueSize;
    }

    public void setClientQueueSize(int clientQueueSize) {
        this.clientQueueSize = clientQueueSize;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isIncludeCallerData() {
        return includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public WebsocketServerFactory getServerFactory() {
        return serverFactory;
    }

    public void setServerFactory(WebsocketServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
