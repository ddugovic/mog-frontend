package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.Ptype


/**
  *
  */
sealed abstract class PieceFace(val faceId: String) {
  val basePath: String = "assets/img/p/"

  def getImagePath(ptype: Ptype): String = s"${basePath}${faceId}/${ptype.toCsaString}.svg"
}

case object JapaneseOneCharFace extends PieceFace("jp1")

object PieceFace {
  def parseString(s: String): Option[PieceFace] = s match {
    case JapaneseOneCharFace.faceId => Some(JapaneseOneCharFace)
    case _ => None
  }
}