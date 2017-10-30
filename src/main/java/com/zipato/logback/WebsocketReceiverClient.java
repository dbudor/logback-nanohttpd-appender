package com.zipato.logback;

import ch.qos.logback.core.net.server.Client;
import ch.qos.logback.core.spi.ContextAware;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

public interface WebsocketReceiverClient extends Client, ContextAware {

    /**
     * Sets the client's event queue.
     * <p>
     * This method must be invoked before the {@link #run()} method is invoked.
     *
     * @param queue the queue to set
     */
    void setQueue(BlockingQueue<Serializable> queue);

    /**
     * Offers an event to the client.
     *
     * @param event the subject event
     * @return {@code true} if the client's queue accepted the event,
     * {@code false} if the client's queue is full
     */
    boolean offer(Serializable event);

    /**
     * Sets the client's serializer.
     */
    void setSerializer(Serializer serializer);

}
