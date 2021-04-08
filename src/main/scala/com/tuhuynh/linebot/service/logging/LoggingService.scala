package com.tuhuynh.linebot.service.logging

import com.tuhuynh.linebot.entity.Event
import org.slf4j.LoggerFactory

object LoggingService {
  private val logger = LoggerFactory.getLogger(classOf[LINEBotLogging])

  def log(event: Event): Unit = {
    logger.info("Event Type: " + event.`type`)
    logger.info("Event Source Type: " + event.source.`type`)
    logger.info("Event Source UserId: " + event.source.userId)
    logger.info("Event Message Text: " + event.message.text)
  }

  def info(obj: Object): Unit = {
    logger.info(obj.toString)
  }

  def error(exception: Exception): Unit = {
    logger.error(exception.getMessage, exception)
  }

  case class LINEBotLogging()
}
