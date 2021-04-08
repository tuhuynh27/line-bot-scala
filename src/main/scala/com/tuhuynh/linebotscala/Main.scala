package com.tuhuynh.linebotscala

import com.jinyframework.HttpServer
import com.jinyframework.core.AbstractRequestBinder.HttpResponse._
import com.tuhuynh.linebotscala.factory.AppContext
import com.tuhuynh.linebotscala.handler.WebhookHandler

import java.util

object Main extends App {
  def log(message: String, level: String = "INFO"): Unit = println(s"$level: $message")

  val server = HttpServer.port(1234)

  // val env = System.getenv()
  // val token = env.get("TOKEN")
  val token = ""
  if (token == null) {
    log("Missing token env")
    System.exit(1)
  }

  val respHeaders = new util.TreeMap[String, String]()
  respHeaders.put("content-type", "application/json; charset=utf-8")
  server.useResponseHeaders(respHeaders)

  Runtime.getRuntime.addShutdownHook(new Thread(() => server.stop()))
  log("Added shutdown hook")

  val webhookHandler = new WebhookHandler(token)

  server.useTransformer(s => AppContext.getGson.toJson(s))
  server.get("/", _ => of("Hello Scala"))
  server.post("/webhook", ctx => webhookHandler.handleWebhook(ctx))
  server.start()
}
