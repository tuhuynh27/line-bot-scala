package com.tuhuynh.linebotscala.handler

import com.jinyframework.HttpClient
import com.tuhuynh.linebotscala.entity._
import com.tuhuynh.linebotscala.factory.AppContext
import com.tuhuynh.linebotscala.service.logging.LoggingService

import java.util

abstract class LINEBotWebhookHandler(token: String) {
  protected var event: Option[Event] = None

  def initEvent(body: String): Unit = {
    val webhookEventObject: WebhookEventObject = AppContext.gson.fromJson(body, classOf[WebhookEventObject])
    event = Some(webhookEventObject.events(0))
    LoggingService.log(event.get)
  }

  def getProfile: UserProfile = {
    val headers = new util.HashMap[String, String]
    headers.put("Authorization", "Bearer " + token)
    val response = HttpClient.builder
      .method("GET").url("https://api.line.me/v2/bot/profile/" + event.get.source.userId)
      .headers(headers)
      .build.perform
    if (response.getStatus == 200) {
      return AppContext.gson.fromJson(response.getBody, classOf[UserProfile])
    }
    throw new Exception("Cannot get user profile")
  }

  def reply(text: String): Unit = {
    val headers = new util.HashMap[String, String]
    headers.put("Content-Type", "application/json")
    headers.put("Authorization", "Bearer " + token)
    val message = Messages("text", text)
    val replyObject = ReplyObject(event.get.replyToken, Array(message))
    val replyObjectJson = AppContext.gson.toJson(replyObject)
    HttpClient.builder.method("POST").url("https://api.line.me/v2/bot/message/reply")
      .headers(headers).body(replyObjectJson)
      .build.perform
    LoggingService.info("Replied: " + text)
  }
}
