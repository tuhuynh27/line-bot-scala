package com.tuhuynh.linebot.entities;

import lombok.Data;

@Data
public class Event {
    private String type;
    private String replyToken;
    private Source source;
    private long timestamp;
    private String mode;
    private Message message;
}
