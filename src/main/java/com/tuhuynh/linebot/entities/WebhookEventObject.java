package com.tuhuynh.linebot.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class WebhookEventObject {
    private Event[] events;
    private String destination;
}
