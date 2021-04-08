package com.tuhuynh.linebotscala.service.dict

import com.tuhuynh.linebotscala.factory.AppContext
import com.tuhuynh.linebotscala.service.logging.LoggingService

import java.io.PrintWriter
import scala.collection.mutable

object DictService {
  private val dict: mutable.Map[String, String] = read()

  def get(key: String): Option[String] = {
    dict.get(key)
  }

  def put(key: String, value: String): Unit = {
    dict.put(key, value)
    sync()
  }

  def all(): mutable.Map[String, String] = dict

  def read(): mutable.Map[String, String] = {
    try {
      val source = scala.io.Source.fromFile("data.json")
      val content = source.mkString
      source.close()
      AppContext.getGson.fromJson(content, classOf[mutable.Map[String, String]])
    } catch {
      case _: Exception => mutable.HashMap()
    }
  }

  def sync(): Unit = {
    val out = new PrintWriter("data.json")
    out.print(AppContext.getGson.toJson(dict))
    out.flush()
    out.close()
    LoggingService.log("Sync data.json")
  }
}
