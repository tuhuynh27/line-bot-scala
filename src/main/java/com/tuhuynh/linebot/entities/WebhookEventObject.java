package com.tuhuynh.linebot.entities;

import lombok.Data;

@Data
public class WebhookEventObject {
    private Event[] events;
    private String destination;
}
