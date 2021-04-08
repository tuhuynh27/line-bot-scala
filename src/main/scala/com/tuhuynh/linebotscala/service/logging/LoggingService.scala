package com.tuhuynh.linebotscala.service.logging

import com.tuhuynh.linebotscala.entity.Event
import org.slf4j.LoggerFactory

object LoggingService {
  case class LINEBotLogging()
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
}
