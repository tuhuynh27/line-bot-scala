package com.tuhuynh.linebot.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReplyObject {
    private String replyToken;
    private Messages[] messages;

    @Data
    @Builder
    public static class Messages {
        private String type;
        private String text;
    }
}
