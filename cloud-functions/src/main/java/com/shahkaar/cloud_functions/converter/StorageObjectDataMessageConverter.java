package com.shahkaar.cloud_functions.converter;

import com.google.events.cloud.storage.v1.StorageObjectData;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.springframework.cloud.function.context.config.JsonMessageConverter;
import org.springframework.cloud.function.json.JsonMapper;
import org.springframework.messaging.Message;

import java.io.BufferedReader;
import java.util.*;
import java.util.stream.Collectors;

public class StorageObjectDataMessageConverter extends JsonMessageConverter {

    public StorageObjectDataMessageConverter(JsonMapper jsonMapper) {
        super(jsonMapper);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return (StorageObjectData.class.equals(clazz));
    }

    @Override
    protected Object convertFromInternal(
            Message<?> message, Class<?> targetClass, Object conversionHint) {

        StorageObjectData.Builder builder = StorageObjectData.newBuilder();

        final var payloadObj = message.getPayload();
        String payloadString;
        if (payloadObj instanceof String s) {
            // when run on GCP we get a String here
            payloadString = s;
        } else if (payloadObj instanceof BufferedReader reader) {
            // when testing locally we get a org.eclipse.jetty.server.Request$1, that can be cast to
            // BufferedReader
            payloadString = readLinesAndJoin(reader);
        } else {
            throw new RuntimeException("Unknown payload type");
        }

        try {
            JsonFormat.parser().merge(payloadString, builder);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }

    private String readLinesAndJoin(BufferedReader reader) {
        return Optional.of(reader)
                .map(BufferedReader::lines)
                .map(lines -> lines.collect(Collectors.joining(System.lineSeparator())))
                .orElse("");
    }
}
