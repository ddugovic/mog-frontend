package com.mogproject.mogami.frontend.view.button

import com.mogproject.mogami.frontend.view.bootstrap.Tooltip
import com.mogproject.mogami.frontend.view.event.EventManageable
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend._
import org.scalajs.dom.Node
import org.scalajs.dom.html.Button
import org.scalajs.dom.raw.HTMLElement

import scalatags.JsDom.all._

/**
  * Single button
  */
case class SingleButton(
                         labels: Map[Language, Node],
                         buttonClass: Seq[String] = Seq("btn-default"),
                         buttonWidth: Option[Int] = None,
                         clickAction: Option[() => Unit] = None,
                         holdAction: Option[() => Unit] = None,
                         holdCheck: () => Boolean = () => false,
                         tooltip: Map[Language, String] = Map.empty,
                         tooltipPlacement: String = "bottom",
                         isBlockButton: Boolean = false,
                         useOnClick: Boolean = true,
                         dismissModal: Boolean = false
                       ) extends WebComponent with EventManageable {

  private[this] val btn: Button = button(
    cls := (("btn" +: buttonClass) ++ isBlockButton.option("btn-block")).mkString(" "),
    buttonWidth.map(width := _),
    tooltip.nonEmpty.option(Seq(data("toggle") := "tooltip", data("placement") := tooltipPlacement)),
    dismissModal.option(data("dismiss") := "modal")
  ).render

  override lazy val element: HTMLElement = {
    // set event handlers
    (clickAction, holdAction, useOnClick) match {
      case (Some(f), Some(g), _) => setClickEvent(btn, f, Some(g), Some(holdCheck))
      case (Some(f), None, true) => btn.onclick = _ => f()
      case (Some(f), None, false) => setClickEvent(btn, f)
      case _ => // do nothing
    }

    // enable tooltip
    if (tooltip.nonEmpty) Tooltip.enableHoverToolTip(btn)

    // set label
    updateLabel(English)

    btn
  }

  def updateLabel(lang: Language): Unit = {
    labels.get(lang) match {
      case Some(lbl) => btn.innerHTML = ""; btn.appendChild(lbl)
      case None => // do nothing
    }
    tooltip.get(lang) match {
      case Some(txt) => btn.setAttribute(data("original-title").name, txt)
      case None => // do nothing
    }
  }
}

