package com.tuhuynh.linebotscala.service.feedback

import com.tuhuynh.linebotscala.factory.AppContext
import com.tuhuynh.linebotscala.service.logging.LoggingService

import scala.collection.mutable

object FeedBackService {
  private val store = AppContext.feedbackStore
  private val tempt: mutable.Map[String, String] = mutable.HashMap()

  private def get(key: String): Short = this.synchronized {
    store.getShort(key)
  }

  private def put(key: String, value: Short): Unit = this.synchronized {
    store.putShort(key, value)
  }

  def openSession(userId: String): Unit = {
    put(userId, 1)
    tempt.put(userId, "")
  }

  def closeSession(userId: String): Unit = {
    put(userId, 0)
    val feedback = tempt.get(userId)
    LoggingService.info(feedback.get)
    tempt.put(userId, "")
  }

  def isSessionOpening(userId: String): Boolean = {
    val got = get(userId)
    if (got == 1) true else false
  }

  def pushFeedback(userId: String, content: String): Unit = {
    val got = tempt.get(userId)
    if (got.get.nonEmpty) {
      val newValue = got.get + "\n" + content
      tempt.put(userId, newValue)
    } else {
      tempt.put(userId, content)
    }
  }
}
