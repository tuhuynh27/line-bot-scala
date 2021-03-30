package com.tuhuynh.linebotscala.handlers

import com.google.gson.Gson
import com.jinyframework.HttpClient
import com.jinyframework.core.AbstractRequestBinder.Context
import com.jinyframework.core.AbstractRequestBinder.HttpResponse
import com.jinyframework.core.AbstractRequestBinder.HttpResponse._
import com.tuhuynh.linebotscala.entities.{Event, Messages, ReplyObject, SimsimiResponse, UserProfile, WebhookEventObject}

import java.util
import scala.collection.mutable

class WebhookHandler(val token: String) {
  private val gson = new Gson()
  private val trashtalkMode = false
  private val teachDict: mutable.Map[String, String] = mutable.HashMap()
  private val dictQueue = new util.LinkedList[String]

  def getWebHookEventObject(body: String): Event = {
    val webhookEventObject: WebhookEventObject = gson.fromJson(body, WebhookEventObject.getClass)
    webhookEventObject.events(0)
  }

  def getProfile(event: Event): UserProfile = {
    val headers = new util.HashMap[String, String]
    headers.put("Authorization", "Bearer " + token)
    val response = HttpClient.builder
      .method("GET").url("https://api.line.me/v2/bot/profile/" + event.source.userId)
      .headers(headers)
      .build.perform
    if (response.getStatus == 200) {
      return gson.fromJson(response.getBody, UserProfile.getClass)
    }
    throw new Exception("Cannot get user profile")
  }

  def getTrashtalk(msg: String): String = {
    val result = HttpClient.builder.method("GET")
      .url("https://simsumi.herokuapp.com/api?text=" + msg.replace(" ", "+") + "&lang=vi")
      .build.perform
    val body = result.getBody
    val simObj: SimsimiResponse = gson.fromJson(body, SimsimiResponse.getClass)
    simObj.success
  }

  def reply(event: Event, text: String): Unit = {
    val headers = new util.HashMap[String, String]
    headers.put("Content-Type", "application/json")
    headers.put("Authorization", "Bearer " + token)
    val message = Messages("text", text)
    val replyObject = ReplyObject(event.replyToken, Array(message))
    val replyObjectJson = gson.toJson(replyObject)
    HttpClient.builder.method("POST").url("https://api.line.me/v2/bot/message/reply")
      .headers(headers).body(replyObjectJson)
      .build.perform
  }

  def handleWebhook(context: Context): HttpResponse = {
    val event = getWebHookEventObject(context.getBody)
    val textOrig = event.message.text.trim
    val text = event.message.text.trim.toLowerCase

    if (text == "bot") {
      reply(event, "Hihi")
      of("OK")
    }

    if (trashtalkMode) {
      if (text == "stop trash") {
        reply(event, "Đã dừng trashtalk ạ!")
        return of("OK")
      }

      val trashtalkText = getTrashtalk(text)
      reply(event, trashtalkText)
      return of("OK")
    }

    if (dictQueue.isEmpty && text == "dạy bot") {
      val profile = getProfile(event)
      dictQueue.addLast(profile.displayName)
      reply(event, "Bạn muốn dạy cho từ gì?")
    } else if (dictQueue.size == 1) {
      val profile = getProfile(event)
      if (profile.displayName == dictQueue.getFirst) {
        dictQueue.addLast(text)
        reply(event, "Bạn muốn nó trả lời sao?")
      } else {
        dictQueue.clear()
      }
    } else if (dictQueue.size == 2) {
      val profile = getProfile(event)
      if (profile.displayName == dictQueue.getFirst) {
        dictQueue.removeFirst()
        val key = dictQueue.removeFirst()
        teachDict.put(key, textOrig)
        reply(event, "Đã dạy xong!")
      } else {
        dictQueue.clear()
      }
    } else {
      val matched = teachDict.get(text)
      if (matched.isDefined) {
        reply(event, matched.get)
      }
    }

    of("OK")
  }

  def showDict(context: Context): HttpResponse = {
    of(gson.toJson(teachDict))
  }
}
