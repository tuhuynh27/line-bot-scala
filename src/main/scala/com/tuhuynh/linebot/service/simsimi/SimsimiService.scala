package com.tuhuynh.linebot.service.simsimi

import com.jinyframework.HttpClient
import com.tuhuynh.linebot.factory.AppContext
import com.tuhuynh.linebot.service.logging.LoggingService
import com.tuhuynh.linebot.service.simsimi.entity.SimsimiResponse

object SimsimiService {
  def getTrashtalk(msg: String): String = {
    LoggingService.info("call getTrashtalk for " + msg)

    try {
      val result = HttpClient.builder.method("GET")
        .url("https://simsumi.herokuapp.com/api?text=" + msg.replace(" ", "+") + "&lang=vi")
        .build.perform
      val body = result.getBody
      val simObj: SimsimiResponse = AppContext.gson.fromJson(body, classOf[SimsimiResponse])
      simObj.success
    } catch {
      case exception: Exception => exception.getMessage
    }
  }
}
