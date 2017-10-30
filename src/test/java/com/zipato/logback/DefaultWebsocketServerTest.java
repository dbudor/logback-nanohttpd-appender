package com.zipato.logback;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Random;

import static org.junit.Assert.*;

public class DefaultWebsocketServerTest {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebsocketServerTest.class);

    final String[] dictionary = ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
            "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
            "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit " +
            "esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
            "qui officia deserunt mollit anim id est laborum.")
            .toLowerCase()
            .replaceAll("[.,]", "")
            .split("\\s+");

    SecureRandom rnd = new SecureRandom();

    String randomString(int len) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i++) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(dictionary[rnd.nextInt(dictionary.length)]);
        }
        return sb.toString();
    }

    @Test
    public void test() throws InterruptedException, URISyntaxException, IOException {
        LOG.info("start");
        if(Desktop.isDesktopSupported())
        {
            Desktop.getDesktop().browse(new URI("http://localhost:7777/html"));
            Desktop.getDesktop().browse(new URI("http://localhost:7777/json"));
        }
        for (int i = 0; i < 1000; i++) {
            String garbage = randomString(2 + rnd.nextInt(4));
            MDC.put("i", "" + i);
            switch (rnd.nextInt(6)) {
                case 0:
                    LOG.trace("trace: {}", garbage);
                    break;
                case 1:
                    LOG.debug("debug: {}", garbage);
                    break;
                case 2:
                    LOG.info("info: {}", garbage);
                    break;
                case 3:
                    LOG.warn("warn: {}", garbage);
                    break;
                case 4:
                    LOG.error("error: {}", garbage);
                    break;
                case 5:
                    try {
                        throw new RuntimeException(garbage);
                    } catch (Exception e) {
                        LOG.error("exception", e);
                    }
                    break;
            }
            MDC.clear();
            Thread.sleep(1000);
        }

    }

}