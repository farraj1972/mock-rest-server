package com.mockserver.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JsonUtils() {
    }

    public static String pretty(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        try {
            Object json = MAPPER.readValue(value, Object.class);
            return MAPPER.writeValueAsString(json);
        } catch (Exception ex) {
            return value;
        }
    }
}