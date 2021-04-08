package com.tuhuynh.linebotscala.entity

case class Event(`type`: String, replyToken: String, source: Source, timestamp: Long, mode: String, message: Message)
