package com.tuhuynh.linebot.entities;

import lombok.Data;

@Data
public class Message {
    String type;
    String id;
    String text;
}
