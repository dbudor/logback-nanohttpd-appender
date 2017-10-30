package com.zipato.logback;

import ch.qos.logback.core.spi.ContextAwareBase;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

public class LoggingWebsocketClient extends ContextAwareBase implements WebsocketReceiverClient {

    private final LoggingWebSocket socket;
    private BlockingQueue<Serializable> queue;
    private Serializer serializer;

    private boolean running = true;
    private String clientId;

    public LoggingWebsocketClient(LoggingWebSocket socket, String clientId) {
        this.socket = socket;
        this.clientId = clientId;
    }

    /**
     * {@inheritDoc}
     */
    public void setQueue(BlockingQueue<Serializable> queue) {
        this.queue = queue;
    }

    /**
     * {@inheritDoc}
     */
    public boolean offer(Serializable event) {
        if (queue == null) {
            throw new IllegalStateException("client has no event queue");
        }
        return queue.offer(event);
    }

    @Override
    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        running = false;
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        addInfo(clientId + " connected");
        ObjectOutputStream oos = null;
        try {
            try {
                String header = serializer.header();
                if (header != null) {
                    socket.send(header);
                }
            } catch (IOException ex) {
                addError(clientId + ex);
                return;
            }
            while (!Thread.currentThread().isInterrupted()) {
                if (!running) {
                    return;
                }
                try {
                    Serializable event = queue.take();
                    socket.send(serializer.serialize(event));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (IOException ex) {
                    addError(clientId + ex);
                }
            }
        } finally {
            close();
            addInfo(clientId + " connection closed");
        }
    }

}
