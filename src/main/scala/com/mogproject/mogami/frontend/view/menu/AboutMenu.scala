package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend.model.{EditModeType, PlayModeType, ViewModeType}
import org.scalajs.dom.html.Div

import scalatags.JsDom
import scalatags.JsDom.all._

/**
  *
  */
object AboutMenu extends AccordionMenu {
  override lazy val ident: String = "About"
  override lazy val title: String = "About This Site"
  override lazy val icon: String = "info-sign"
  override lazy val content: JsDom.TypedTag[Div] = div(
    p(i(""""Run anywhere. Needs NO installation."""")),
    p("Shogi Playground is a platform for all shogi --Japanese chess-- fans in the world." +
      " This mobile-friendly website enables you to manage, analyze, and share shogi games as well as mate problems."),
    p("If you have any questions, trouble, or suggestion, please tell the ",
      a(href := "https://twitter.com/mogproject", target := "_blank", "author"),
      ". Your voice matters.")
  )
  override lazy val visibleMode = Set(PlayModeType, ViewModeType, EditModeType)
}