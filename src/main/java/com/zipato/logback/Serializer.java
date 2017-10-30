package com.zipato.logback;


import java.io.Serializable;

public interface Serializer {
    String header();

    String serialize(Serializable event);
}
