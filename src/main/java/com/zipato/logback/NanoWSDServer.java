package com.zipato.logback;

import fi.iki.elonen.NanoWSD;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public abstract class NanoWSDServer<T extends ClientWebsocket> extends NanoWSD implements WebsocketServer<T> {

    private Map<String, WebsocketServerListener> listenerMap = new HashMap<String, WebsocketServerListener>();

    public NanoWSDServer(int port) throws IOException {
        super(port);
        start(60000, true);
    }

    @Override
    public void addListener(String path, WebsocketServerListener listener) {
        if (listenerMap.get(path) != null) {
            throw new IllegalArgumentException("there is already listener for path " + path);
        }
        listenerMap.put(path, listener);
    }

    @Override
    public void removeListener(String path) {
        listenerMap.remove(path);
    }

    @Override
    public void close() {
        if (listenerMap.isEmpty()) {
            super.closeAllConnections();
        }
    }

    protected abstract T createWebsocket(IHTTPSession handshake);

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        String uri = handshake.getUri();
        WebsocketServerListener listener = listenerMap.get(uri);
        if (listener == null) {
            return null;
        }
        T socket = createWebsocket(handshake);
        if (listener.offer(socket.getClient())) {
            return (WebSocket) socket;
        }
        return null;
    }

    @Override
    public Response serveHttp(IHTTPSession session) {
        String uri = session.getUri();
        for (Map.Entry<String, WebsocketServerListener> entry: listenerMap.entrySet()) {
            String key = entry.getKey();
            if (uri.startsWith(key)) {
                return serveListener(entry.getValue(), uri.substring(key.length()));
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404");

    }

    protected Response serveListener(WebsocketServerListener listener, String uri) {
        if (uri.isEmpty() || "/".equals(uri)) {
            uri = "index.html";
        }
        String html = listener.getHtml();
        if (html.startsWith("classpath")) {
            return serveClasspath(html, uri);
        } else {
            return serveUrl(html, uri);
        }
    }

    protected Response serveClasspath(String html, String uri) {
        String ct = "application/octet-stream";
        if (uri.endsWith(".html") || uri.endsWith("htm")) {
            ct = "text/html; charset=UTF-8";
        } else if (ct.endsWith(".js")) {
            ct = "application/javasctipt";
        } else if (ct.endsWith(".css")) {
            ct = "text/css";
        }
        String path = html.replaceFirst("classpath:", "/") + uri;
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404");
        }
        return newChunkedResponse(Response.Status.OK, ct, stream);

    }

    protected Response serveUrl(String html, String uri) {
        try {
            URL url = new URL(html + uri);
            URLConnection conn = url.openConnection();
            String ct = conn.getHeaderField("content-type");
            InputStream stream = conn.getInputStream();
            return newChunkedResponse(Response.Status.OK, ct, stream);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, sw.toString());
        }
    }

}
