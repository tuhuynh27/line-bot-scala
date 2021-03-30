package com.tuhuynh.linebotscala.handlers

import com.google.gson.Gson
import com.jinyframework.HttpClient
import com.tuhuynh.linebotscala.entities.{Event, Messages, ReplyObject, WebhookEventObject}

import java.util
import scala.collection.mutable

class WebhookHandler(val token: String) {
  private val gson = new Gson()
  private val data: mutable.Map[String, String] = mutable.HashMap()

  def getWebHookEventObject(body: String): Event = {
    val webhookEventObject: WebhookEventObject = gson.fromJson(body, WebhookEventObject.getClass)
    webhookEventObject.events(0)
  }

  def reply(event: Event, text: String): Unit = {
    val headers = new util.HashMap[String, String]
    headers.put("Content-Type", "application/json")
    headers.put("Authorization", "Bearer " + token)
    val message = Messages("text", text)
    val replyObject = ReplyObject(event.replyToken, Array(message))
    val replyObjectJson = gson.toJson(replyObject)
    HttpClient.builder().method("POST").url("https://api.line.me/v2/bot/message/reply")
      .headers(headers).body(replyObjectJson)
      .build().perform()
  }
}
