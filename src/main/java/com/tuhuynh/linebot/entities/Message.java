package com.tuhuynh.linebot.entities;

import lombok.Data;

@Data
public class Message {
    private String type;
    private String id;
    private String text;
}
