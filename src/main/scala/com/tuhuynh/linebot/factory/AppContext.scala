package com.tuhuynh.linebot.factory

import com.google.gson.Gson
import com.jinyframework.keva.store.{NoHeapStore, NoHeapStoreManager}

object AppContext {
  private val storeManager = new NoHeapStoreManager
  private var gsonInstance = new Gson

  def gson: Gson = gsonInstance
  storeManager.createStore("Dict", NoHeapStore.Storage.PERSISTED, 64)
  def dictStore: NoHeapStore = storeManager.getStore("Dict")
}
