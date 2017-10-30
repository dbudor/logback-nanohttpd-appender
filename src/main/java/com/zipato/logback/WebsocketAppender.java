package com.zipato.logback;

import ch.qos.logback.classic.net.LoggingEventPreSerializationTransformer;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import ch.qos.logback.core.status.ErrorStatus;

import java.io.OutputStream;

public class WebsocketAppender<E> extends WebsocketAppenderBase<ILoggingEvent> {
    private static final PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();

    /**
     * It is the encoder which is ultimately responsible for writing the event to
     * an {@link OutputStream}.
     */
    private Encoder<ILoggingEvent> encoder;

    protected void postProcessEvent(ILoggingEvent event) {
        if (isIncludeCallerData()) {
            event.getCallerData();
        }
    }

    @Override
    public PreSerializationTransformer<ILoggingEvent> getPST() {
        return pst;
    }

    @Override
    public void start() {
        int errors = 0;
        if (getSerializer() == null) {
            if (this.encoder == null) {
                addStatus(new ErrorStatus("No encoder set for the appender named \"" + name + "\".", this));
                errors++;
            }
            setSerializer(new EncoderWrappingSerializer(encoder));
        }
        // only error free appenders should be activated
        if (errors == 0) {
            super.start();
        }
    }

    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }
}
