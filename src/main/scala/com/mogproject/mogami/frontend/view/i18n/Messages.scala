package com.mogproject.mogami.frontend.view.i18n

import com.mogproject.mogami.State
import com.mogproject.mogami.frontend.Language
import com.mogproject.mogami.frontend.model.{English, Japanese}

/**
  * Message definitions
  */
trait Messages {
  //
  // Analyze
  //
  def ANALYZE: String

  def COUNT_POINT: String

  def COMMENT_CLEAR: String

  def COMMENT_UPDATE: String


  //
  // Reset
  //
  def INITIAL_STATE: Map[State, String]

}

object Messages {
  private[this] var currentLanguage: Language = English

  private[this] var messageSet: Messages = MessagesEnglish

  def setLanguage(language: Language): Unit = {
    currentLanguage = language

    messageSet = language match {
      case Japanese => MessagesJapanese
      case _ => MessagesEnglish
    }
  }

  def getLanguage: Language = currentLanguage

  def get: Messages = messageSet
}