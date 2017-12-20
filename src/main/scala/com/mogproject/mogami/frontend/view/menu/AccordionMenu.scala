package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.model.ModeType
import com.mogproject.mogami.frontend.view.bootstrap.Tooltip
import com.mogproject.mogami.frontend.view.{Observable, WebComponent}
import org.scalajs.dom.html.Div
import com.mogproject.mogami.util.Implicits._

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery


/**
  *
  */
trait AccordionMenu extends WebComponent with Observable[AccordionMenu] {

  def ident: String

  def title: String

  def icon: String

  def content: TypedTag[Div]

  def visibleMode: Set[ModeType]

  private[this] val panelCls = Map(false -> "panel-default", true -> "panel-info")

  private[this] lazy val glyph = span(cls := s"glyphicon glyphicon-${icon}").render

  private[this] lazy val mainElem: Div = div(
    id := s"collapse${ident}",
    cls := "panel-collapse collapse",
    role := "tabpanel",
    aria.labelledby := s"heading${ident}",
    div(
      cls := "panel-body",
      content
    )
  ).render

  private[this] val titleElem = span().render

  private[this] val titleElemHeading = h4(cls := "panel-title",
    span(
      cls := "accordion-toggle",
      glyph,
      titleElem
    )
  ).render

  override lazy val element: Div = {
    val elem = div(
      cls := "panel",
      data("toggle") := "tooltip",
      data("placement") := "left",
      marginBottom := 5.px,
      div(
        cls := "panel-heading",
        id := s"heading${ident}",
        role := "button",
        data("toggle") := "collapse",
        data("target") := s"#collapse${ident}",
        data("parent") := "#accordion",
        titleElemHeading
      ),
      mainElem
    ).render

    Tooltip.enableHoverToolTip(elem)
    elem
  }

  def initialize(): Unit = {
    def f(b: Boolean): Unit = {
      element.classList.remove(panelCls(!b))
      element.classList.add(panelCls(b))
    }

    // set events
    jQuery(mainElem)
      .on("show.bs.collapse", { () => f(true); notifyObservers(this) })
      .on("hide.bs.collapse", () => f(false))

    expandTitle()
  }

  def collapseTitle(): Unit = {
    titleElem.style.paddingLeft = 0.px
    titleElem.innerHTML = ""
    element.setAttribute("data-original-title", (ident == "EditHelp").fold("Help", ident))
  }

  def expandTitle(): Unit = {
    titleElem.style.paddingLeft = 20.px
    titleElem.innerHTML = " " + title
    element.removeAttribute("data-original-title")
  }

  def refresh(modeType: ModeType): Unit = {
    if (visibleMode.contains(modeType)) show() else hide()
  }

  initialize()

}
