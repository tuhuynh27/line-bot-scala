package com.tuhuynh.linebot.service.dict

import com.tuhuynh.linebot.factory.AppContext

object DictService {
  private val store = AppContext.dictStore

  def get(key: String): String = store.getString(key)

  def put(key: String, value: String): Unit = {
    store.putString(key, value)
  }
}
