package com.shahkaar.cloud_functions.data;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class PubSubBody {

    private PubSubMessage message;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class PubSubMessage {
        private String data;
        private Map<String, String> attributes;
        private String messageId;
        private String publishTime;
    }
}