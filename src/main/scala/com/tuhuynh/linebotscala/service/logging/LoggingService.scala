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

  def log(text: String): Unit = {
    logger.info(text)
  }
}
