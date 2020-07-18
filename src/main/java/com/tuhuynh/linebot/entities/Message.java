package com.tuhuynh.linebot.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Message {
    private String type;
    private String id;
    private String text;
}
