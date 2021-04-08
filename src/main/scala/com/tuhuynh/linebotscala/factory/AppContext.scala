package com.tuhuynh.linebotscala.factory

import com.google.gson.Gson

object AppContext {
  private var gsonInstance = new Gson

  def getGson: Gson = gsonInstance
  def setGson(gson: Gson): Unit = {
    gsonInstance = gson
  }
}
