package com.tuhuynh.linebot.entities;

import lombok.Data;

@Data
public class UserProfile {
    String displayName;
    String userId;
    String pictureUrl;
    String statusMessage;
}
