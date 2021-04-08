package com.tuhuynh.linebotscala.handler

import com.jinyframework.core.AbstractRequestBinder.HttpResponse._
import com.jinyframework.core.AbstractRequestBinder.{Context, HttpResponse}
import com.tuhuynh.linebotscala.entity.JSONResponse
import com.tuhuynh.linebotscala.service.dict.DictService
import com.tuhuynh.linebotscala.service.feedback.FeedBackService
import com.tuhuynh.linebotscala.service.logging.LoggingService
import com.tuhuynh.linebotscala.service.simsimi.SimsimiService

import java.util

class WebhookHandler(val token: String) extends LINEBotWebhookHandler(token) {
  private var trashtalkMode = false
  private val dictQueue = new util.LinkedList[String]

  def handleWebhook(context: Context): HttpResponse = {
    initEvent(context.getBody)

    val textOrig = event.get.message.text.trim
    val text = textOrig.toLowerCase

    if (text == "bot") {
      reply("Hihi")
      return of(JSONResponse("OK"))
    }

    // Feedback Service
    if (event.get.source.`type` == "user") {
      val userId = getProfile.userId
      if (FeedBackService.isSessionOpening(userId)) {
        if (text == "end feedback") {
          FeedBackService.closeSession(userId)
          reply("Cảm ơn bạn!")
          return of(JSONResponse("OK"))
        }
        // Record feedback
        FeedBackService.pushFeedback(userId, text)
      } else {
        if (text == "feedback") {
          FeedBackService.openSession(userId)
          reply("Xin chào bạn, mời bạn feedback cho HR team")
          return of(JSONResponse("OK"))
        }
      }
    }

    if (!trashtalkMode && text == "trash") {
      trashtalkMode = true
      LoggingService.info("trashtalkMode = true")
      reply("OK!")
      return of(JSONResponse("OK"))
    }
    if (trashtalkMode) {
      if (text == "stop trash") {
        trashtalkMode = false
        LoggingService.info("trashtalkMode = false")
        reply("Đã dừng trashtalk ạ!")
        return of(JSONResponse("OK"))
      }

      val trashtalkText = SimsimiService.getTrashtalk(text)
      reply(trashtalkText)
      return of(JSONResponse("OK"))
    }

    if (dictQueue.isEmpty && text == "dạy bot") {
      val profile = getProfile
      dictQueue.addLast(profile.displayName)
      reply("Bạn muốn dạy cho từ gì?")
    } else if (dictQueue.size == 1) {
      val profile = getProfile
      if (profile.displayName == dictQueue.getFirst) {
        dictQueue.addLast(text)
        reply("Bạn muốn nó trả lời sao?")
      } else {
        dictQueue.clear()
      }
    } else if (dictQueue.size == 2) {
      val profile = getProfile
      if (profile.displayName == dictQueue.getFirst) {
        dictQueue.removeFirst()
        val key = dictQueue.removeFirst()
        DictService.put(key, textOrig)
        reply("Đã dạy xong!")
      } else {
        dictQueue.clear()
      }
    } else {
      val matched = DictService.get(text)
      if (matched != null && matched.nonEmpty) {
        reply(matched)
      }
    }

    of(JSONResponse("OK"))
  }
}
