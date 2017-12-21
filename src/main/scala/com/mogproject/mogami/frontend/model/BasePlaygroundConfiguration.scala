package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.frontend.LocalStorage
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.frontend.api.MobileScreen
import com.mogproject.mogami.frontend.model.DeviceType.DeviceType
import com.mogproject.mogami.frontend.model.board.{DoubleBoard, FlipDisabled, FlipEnabled, FlipType}
import com.mogproject.mogami.frontend.view.board.{SVGAreaLayout, SVGStandardLayout}
import com.mogproject.mogami.frontend.view.sidebar.{SideBarLeft, SideBarRight}
import org.scalajs.dom

import scala.scalajs.js.UndefOr

/**
  * Base configuration for Playground framework
  */
case class BasePlaygroundConfiguration(layout: SVGAreaLayout = SVGStandardLayout,
                                       pieceWidth: Option[Int] = None,
                                       flipType: FlipType = FlipDisabled,
                                       pieceFace: PieceFace = JapaneseOneCharFace,
                                       newBranchMode: Boolean = false,
                                       messageLang: Language = English,
                                       recordLang: Language = Japanese,
                                       visualEffectEnabled: Boolean = true,
                                       soundEffectEnabled: Boolean = false,
                                       baseUrl: String = "",
                                       deviceType: DeviceType = DeviceType.PC,
                                       isDev: Boolean = false,
                                       isDebug: Boolean = false
                                      ) {

  def isAreaFlipped(areaId: Int): Boolean = flipType match {
    case FlipDisabled => false
    case FlipEnabled => true
    case DoubleBoard => areaId == 1
  }

  def toQueryParameters: List[String] = {
    type Parser = List[String] => List[String]

    val parseFlip: Parser = xs => flipType match {
      case FlipEnabled => "flip=true" :: xs
      case _ => xs
    }

    parseFlip(List.empty)
  }

  def updateScreenSize(): BasePlaygroundConfiguration = {
    this.copy(deviceType = deviceType.setLandscape(BasePlaygroundConfiguration.getIsLandscape), pieceWidth = None)
  }

  def collapseByDefault: Boolean = {
    pieceWidth.map(layout.areaWidth).exists { w =>
      !deviceType.isMobile && BasePlaygroundConfiguration.getClientWidth < w + SideBarLeft.EXPANDED_WIDTH + SideBarRight.EXPANDED_WIDTH
    }
  }

  def loadLocalStorage(): BasePlaygroundConfiguration = {
    val ls = LocalStorage.load()
    this.copy(
      pieceWidth = ls.pieceWidth,
      flipType = ls.doubleBoardMode.contains(true).fold(DoubleBoard, flipType),
      messageLang = ls.messageLang.getOrElse(messageLang),
      recordLang = ls.recordLang.getOrElse(recordLang),
      pieceFace = ls.pieceFace.getOrElse(pieceFace),
      visualEffectEnabled = ls.visualEffect.getOrElse(true)
    )
  }
}

object BasePlaygroundConfiguration {

  private[this] final val LANDSCAPE_MARGIN_HEIGHT: Int = 44
  private[this] final val PORTRAIT_MARGIN_HEIGHT: Int = LANDSCAPE_MARGIN_HEIGHT * 2 + 20

  lazy val browserLanguage: Language = {
    def f(n: UndefOr[String]): Option[String] = n.toOption.flatMap(Option.apply)

    val nav = dom.window.navigator.asInstanceOf[com.mogproject.mogami.frontend.api.Navigator]
    val firstLang = nav.languages.toOption.flatMap(_.headOption)
    val lang: Option[String] = (firstLang ++ f(nav.language) ++ f(nav.userLanguage) ++ f(nav.browserLanguage)).headOption

    lang.map(_.slice(0, 2).toLowerCase) match {
      case Some("ja") => Japanese
      case _ => English
    }
  }

  lazy val defaultBaseUrl = s"${dom.window.location.protocol}//${dom.window.location.host}${dom.window.location.pathname}"

  lazy val defaultIsMobile: Boolean = dom.window.screen.width < 768

  def getIsLandscape: Boolean = MobileScreen.isLandscape

  // possibly using an in-app browser
  private[this] def isInAppBrowser: Boolean = defaultIsMobile && (getIsLandscape ^ dom.window.innerWidth > dom.window.innerHeight)

  def getScreenWidth: Double = (defaultIsMobile, getIsLandscape) match {
    case (true, true) => math.max(dom.window.screen.width, dom.window.screen.height)
    case (true, false) => math.min(dom.window.screen.width, dom.window.screen.height)
    case (false, _) => dom.window.screen.width
  }

  def getScreenHeight: Double = (defaultIsMobile, getIsLandscape) match {
    case (true, true) => math.min(dom.window.screen.width, dom.window.screen.height)
    case (true, false) => math.max(dom.window.screen.width, dom.window.screen.height)
    case (false, _) => dom.window.screen.height
  }

  def getClientWidth: Double = isInAppBrowser.fold(getScreenWidth, dom.window.innerWidth)

  def getClientHeight: Double = if (isInAppBrowser)
    getScreenHeight - getIsLandscape.fold(LANDSCAPE_MARGIN_HEIGHT, PORTRAIT_MARGIN_HEIGHT)
  else
    math.min(dom.window.innerHeight, getScreenHeight - LANDSCAPE_MARGIN_HEIGHT)

  def getDefaultCanvasWidth: Int = getDefaultCanvasWidth(getClientWidth, getClientHeight, getIsLandscape)

  def getDefaultCanvasWidth(clientWidth: Double, clientHeight: Double, isLandscape: Boolean): Int = {
    math.max(100, math.min(math.min(clientWidth - 10, (clientHeight - isLandscape.fold(76, 60)) * 400 / 576).toInt, 400))
  }

}