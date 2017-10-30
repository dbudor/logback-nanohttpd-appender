package com.zipato.logback;

import ch.qos.logback.core.net.server.ConcurrentServerRunner;
import ch.qos.logback.core.net.server.ServerListener;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

public class WebsocketServerRunner extends ConcurrentServerRunner<WebsocketReceiverClient> {

    private final int clientQueueSize;
    private final Serializer serializer;

    /**
     * Constructs a new server runner.
     *
     * @param listener   the listener from which the server will accept new
     *                   clients
     * @param executor   that will be used to execute asynchronous tasks
     *                   on behalf of the runner.
     * @param queueSize  size of the event queue that will be maintained for
     *                   each client
     * @param serializer serializer for the client
     */
    public WebsocketServerRunner(ServerListener<WebsocketReceiverClient> listener, Executor executor, int queueSize, Serializer serializer) {
        super(listener, executor);
        this.clientQueueSize = queueSize;
        this.serializer = serializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean configureClient(WebsocketReceiverClient client) {
        client.setContext(getContext());
        client.setQueue(new ArrayBlockingQueue<Serializable>(clientQueueSize));
        client.setSerializer(serializer);
        return true;
    }


}
