package com.tuhuynh.linebotscala.entities

case class Event(`type`: String, replyToken: String, source: Source, timestamp: Long, mode: String, message: Message)
