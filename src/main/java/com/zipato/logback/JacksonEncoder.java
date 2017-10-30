package com.zipato.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.encoder.EncoderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import org.slf4j.Marker;

import java.util.Date;
import java.util.Map;

public class JacksonEncoder extends EncoderBase<ILoggingEvent> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] headerBytes() {
        return new byte[0];
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        try {
            ObjectNode obj = mapper.createObjectNode();
            obj.put("timestamp", ISO8601Utils.format(new Date(event.getTimeStamp())));
            obj.put("message", event.getFormattedMessage());

            IThrowableProxy tp = event.getThrowableProxy();
            if (tp != null) {
                obj.put("exception", ThrowableProxyUtil.asString(tp));
            }
            obj.put("logger", event.getLoggerName());
            obj.put("thread", event.getThreadName());
            obj.put("level", event.getLevel().toString());
            Marker marker = event.getMarker();
            if (marker != null) {
                obj.put("marker", marker.getName());
            }
            Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
            if (!mdcPropertyMap.isEmpty()) {
                obj.put("mdc", mapper.valueToTree(mdcPropertyMap));
            }
            StackTraceElement[] cd = event.getCallerData();
            if (cd != null) {
                obj.put("stack", mapper.valueToTree(cd));
            }
            return mapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            addError("json encoding failed", e);
            return null;
        }
    }

    @Override
    public byte[] footerBytes() {
        return new byte[0];
    }
}
