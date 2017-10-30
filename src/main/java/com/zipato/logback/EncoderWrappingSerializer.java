package com.zipato.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;

import java.io.Serializable;

public class EncoderWrappingSerializer implements Serializer {

    private Encoder<ILoggingEvent> encoder;

    public EncoderWrappingSerializer() {
    }

    public EncoderWrappingSerializer(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    @Override
    public String header() {
        byte[] headerBytes = encoder.headerBytes();
        if (headerBytes == null || headerBytes.length == 0) {
            return null;
        }
        String header = new String(headerBytes);
        byte[] footerBytes = encoder.footerBytes();
        if (footerBytes != null && footerBytes.length > 0) {
            header += new String(footerBytes);
        }
        return header;
    }

    @Override
    public String serialize(Serializable event) {
        if (event instanceof ILoggingEvent) {
            return new String(encoder.encode((ILoggingEvent) event));
        }
        return null;
    }

    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }
}
