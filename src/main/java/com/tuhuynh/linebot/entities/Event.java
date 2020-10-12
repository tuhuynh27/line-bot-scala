package com.tuhuynh.linebot.entities;

import lombok.Data;

@Data
public class Event {
    String type;
    String replyToken;
    Source source;
    long timestamp;
    String mode;
    Message message;
}
