package com.tuhuynh.linebot.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReplyObject {
    String replyToken;
    Messages[] messages;

    @Data
    @Builder
    public static class Messages {
        String type;
        String text;
    }
}
