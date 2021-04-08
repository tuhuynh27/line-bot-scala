package com.tuhuynh.linebotscala

import com.jinyframework.HttpServer
import com.jinyframework.core.AbstractRequestBinder.HttpResponse._
import com.jinyframework.core.utils.ParserUtils.HttpMethod
import com.tuhuynh.linebotscala.entity.JSONResponse
import com.tuhuynh.linebotscala.factory.AppContext
import com.tuhuynh.linebotscala.handler.WebhookHandler

import java.util

object Main extends App {
  val server = HttpServer.port(1234)
  val env = System.getenv()
  val token = env.get("TOKEN")
  val respHeaders = new util.TreeMap[String, String]()
  if (token == null) {
    log("Missing token env")
    System.exit(1)
  }
  val webhookHandler = new WebhookHandler(token)
  respHeaders.put("content-type", "application/json; charset=utf-8")
  server.useResponseHeaders(respHeaders)

  Runtime.getRuntime.addShutdownHook(new Thread(() => server.stop()))
  log("Added shutdown hook")

  def log(message: String, level: String = "INFO"): Unit = println(s"$level: $message")

  server.useTransformer(s => AppContext.gson.toJson(s))
  server.get("/", _ => of(JSONResponse("Hello World")))
  server.post("/webhook", ctx => webhookHandler.handleWebhook(ctx))
  // 404 handler
  server.addHandler(HttpMethod.ALL, "/**", _ => of(JSONResponse("Not found")))
  server.start()
}
