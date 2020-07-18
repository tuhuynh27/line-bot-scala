package com.tuhuynh.linebot.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Event {
    private String type;
    private String replyToken;
    private Source source;
    private long timestamp;
    private String mode;
    private Message message;
}
