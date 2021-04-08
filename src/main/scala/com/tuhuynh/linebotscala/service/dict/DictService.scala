package com.tuhuynh.linebotscala.service.dict

import com.tuhuynh.linebotscala.factory.AppContext

object DictService {
  private val store = AppContext.dictStore

  def get(key: String): String = store.getString(key)

  def put(key: String, value: String): Unit = {
    store.putString(key, value)
  }
}
