package com.tuhuynh.linebot.service.feedback

import java.io.FileWriter
import java.util.Calendar
import scala.collection.mutable

object FeedbackService {
  private val store: mutable.Map[String, Short] = mutable.HashMap()
  private val tempt: mutable.Map[String, String] = mutable.HashMap()

  def openSession(userId: String): Unit = {
    put(userId, 1)
    tempt.put(userId, "")
  }

  private def put(key: String, value: Short): Unit = this.synchronized {
    store.put(key, value)
  }

  def closeSession(userId: String): Unit = {
    put(userId, 0)
    val feedback = tempt.get(userId)
    writeToFile(feedback.get)
    tempt.put(userId, "")
  }

  def writeToFile(content: String): Unit = {
    val fw = new FileWriter("feedback.txt", true)
    try {
      val time = Calendar.getInstance.getTime.toString + "\n"
      fw.write(time)
      fw.write(content)
      fw.write("\n\n")
    }
    finally fw.close()
  }

  def isSessionOpening(userId: String): Boolean = {
    try {
      val got = get(userId)
      if (got == 1) true else false
    } catch {
      case _: Exception => return false
    }
  }

  private def get(key: String): Short = this.synchronized {
    store(key)
  }

  def pushFeedback(userId: String, content: String): Unit = {
    val got = tempt.get(userId)
    if (got.get.nonEmpty) {
      val newValue = got.get + "\n" + content
      tempt.put(userId, newValue)
    } else {
      tempt.put(userId, content)
    }
  }
}
