package com.tuhuynh.linebotscala.handler

import com.jinyframework.HttpClient
import com.tuhuynh.linebotscala.entity.{Event, Messages, ReplyObject, UserProfile, WebhookEventObject}
import com.tuhuynh.linebotscala.factory.AppContext
import com.tuhuynh.linebotscala.service.logging.LoggingService

import java.util

abstract class AbstractWebhookHandler {
  def getWebHookEventObject(body: String): Event = {
    val webhookEventObject: WebhookEventObject = AppContext.getGson.fromJson(body, classOf[WebhookEventObject])
    webhookEventObject.events(0)
  }

  def getProfile(event: Event, token: String): UserProfile = {
    val headers = new util.HashMap[String, String]
    headers.put("Authorization", "Bearer " + token)
    val response = HttpClient.builder
      .method("GET").url("https://api.line.me/v2/bot/profile/" + event.source.userId)
      .headers(headers)
      .build.perform
    if (response.getStatus == 200) {
      return AppContext.getGson.fromJson(response.getBody, classOf[UserProfile])
    }
    throw new Exception("Cannot get user profile")
  }

  def reply(event: Event, token: String, text: String): Unit = {
    val headers = new util.HashMap[String, String]
    headers.put("Content-Type", "application/json")
    headers.put("Authorization", "Bearer " + token)
    val message = Messages("text", text)
    val replyObject = ReplyObject(event.replyToken, Array(message))
    val replyObjectJson = AppContext.getGson.toJson(replyObject)
    HttpClient.builder.method("POST").url("https://api.line.me/v2/bot/message/reply")
      .headers(headers).body(replyObjectJson)
      .build.perform
    LoggingService.log("Replied: " + text)
  }
}
