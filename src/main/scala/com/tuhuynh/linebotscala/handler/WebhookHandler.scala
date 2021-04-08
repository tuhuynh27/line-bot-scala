package com.tuhuynh.linebotscala.handler

import com.jinyframework.core.AbstractRequestBinder.HttpResponse._
import com.jinyframework.core.AbstractRequestBinder.{Context, HttpResponse}
import com.tuhuynh.linebotscala.factory.AppContext
import com.tuhuynh.linebotscala.service.dict.DictService
import com.tuhuynh.linebotscala.service.logging.LoggingService
import com.tuhuynh.linebotscala.service.simsimi.SimsimiService

import java.util

class WebhookHandler(val token: String) extends AbstractWebhookHandler {
  private var trashtalkMode = false
  private val dictQueue = new util.LinkedList[String]

  def handleWebhook(context: Context): HttpResponse = {
    val event = getWebHookEventObject(context.getBody)
    LoggingService.log(event)

    val textOrig = event.message.text.trim
    val text = textOrig.toLowerCase

    if (text == "bot") {
      reply(event, token, "Hihi")
      return of("OK")
    }

    if (!trashtalkMode && text == "trash") {
      trashtalkMode = true
      LoggingService.log("trashtalkMode = true")
      reply(event, token, "OK!")
      return of ("OK")
    }

    if (trashtalkMode) {
      if (text == "stop trash") {
        trashtalkMode = false
        LoggingService.log("trashtalkMode = false")
        reply(event, token, "Đã dừng trashtalk ạ!")
        return of("OK")
      }

      val trashtalkText = SimsimiService.getTrashtalk(text)
      reply(event, token, trashtalkText)
      return of("OK")
    }

    if (dictQueue.isEmpty && text == "dạy bot") {
      val profile = getProfile(event, token)
      dictQueue.addLast(profile.displayName)
      reply(event, token, "Bạn muốn dạy cho từ gì?")
    } else if (dictQueue.size == 1) {
      val profile = getProfile(event, token)
      if (profile.displayName == dictQueue.getFirst) {
        dictQueue.addLast(text)
        reply(event, token, "Bạn muốn nó trả lời sao?")
      } else {
        dictQueue.clear()
      }
    } else if (dictQueue.size == 2) {
      val profile = getProfile(event, token)
      if (profile.displayName == dictQueue.getFirst) {
        dictQueue.removeFirst()
        val key = dictQueue.removeFirst()
        DictService.put(key, textOrig)
        reply(event, token, "Đã dạy xong!")
      } else {
        dictQueue.clear()
      }
    } else {
      val matched = DictService.get(text)
      if (matched.isDefined) {
        reply(event, token, matched.get)
      }
    }

    of("OK")
  }

  def showDict(context: Context): HttpResponse = {
    of(AppContext.getGson.toJson(DictService.all()))
  }
}
