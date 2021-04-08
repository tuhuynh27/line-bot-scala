package com.tuhuynh.linebotscala.factory

import com.google.gson.Gson
import com.jinyframework.keva.store.{NoHeapStore, NoHeapStoreManager}

object AppContext {
  private var gsonInstance = new Gson
  def gson: Gson = gsonInstance

  private val storeManager = new NoHeapStoreManager
  storeManager.createStore("Dict", NoHeapStore.Storage.PERSISTED, 64)
  storeManager.createStore("Feedback", NoHeapStore.Storage.PERSISTED, 64)
  def dictStore: NoHeapStore = storeManager.getStore("Dict")
  def feedbackStore: NoHeapStore = storeManager.getStore("Feedback")
}
