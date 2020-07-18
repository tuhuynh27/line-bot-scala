package com.tuhuynh.linebot.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Source {
    private String userId;
    private String type;
}
