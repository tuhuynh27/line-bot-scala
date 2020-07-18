package com.tuhuynh.linebot.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ReplyObject {
    private String replyToken;
    private Messages[] messages;

    @Builder
    @Getter
    @Setter
    public static class Messages {
        private String type;
        private String text;
    }
}
