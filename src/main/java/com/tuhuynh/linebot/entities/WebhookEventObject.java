package com.tuhuynh.linebot.entities;

import lombok.Data;

@Data
public class WebhookEventObject {
    Event[] events;
    String destination;
}
