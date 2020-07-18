package com.tuhuynh.linebot.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserProfile {
    private String displayName;
    private String userId;
    private String pictureUrl;
    private String statusMessage;
}
