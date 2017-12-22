package com.mogproject.mogami.frontend

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.state.StateCache.Implicits._
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.state.TestState
import com.mogproject.mogami.frontend.view.TestView
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.util.{Failure, Success, Try}

/**
  * Entry point for testing
  */
object App extends JSApp {
  override def main(): Unit = {
    // get args
    val args = Arguments()
      .loadLocalStorage()
      .parseQueryString(dom.window.location.search)

    // load game
    val game = createGameFromArgs(args)

    // update mode
    val isSnapshot = game.trunk.moves.isEmpty && game.trunk.finalAction.isEmpty && game.branches.isEmpty

    val mode = isSnapshot.fold(
      PlayMode(GameControl(game, 0, 0)),
      ViewMode(GameControl(game, args.gamePosition.branch, math.max(0, args.gamePosition.position - game.trunk.offset)))
    )

    // create model
    val model = TestModel(mode, args.config)

    // create view
    val view = TestView(args.config.deviceType.isMobile, dom.document.getElementById("app"))

    // handle special actions
    args.action match {
      case NotesAction =>
        view.drawNotes(game, args.config.recordLang)
      case ImageAction =>
        PlaygroundSAM.initialize(TestModel.adapter)
        SAM.initialize(TestState(model, view))
        view.drawAsImage()
      case PlayAction =>
        // initialize state
        PlaygroundSAM.initialize(TestModel.adapter)
        SAM.initialize(TestState(model, view))
    }
  }

  private[this] def createGameFromArgs(args: Arguments): Game = {
    def loadGame(game: => Game): Game = Try(game) match {
      case Success(g) => g
      case Failure(e) =>
        println(s"Failed to create a game: ${e}")
        Game()
    }

    val gg: Game = ((args.usen, args.sfen) match {
      case (Some(u), _) => loadGame(Game.parseUsenString(u)) // parse USEN string
      case (_, Some(s)) => loadGame(Game.parseSfenString(s)) // parse SFEN string
      case _ => Game()
    }).copy(newGameInfo = args.gameInfo)

    // update comments
    val comments = for {
      (b, m) <- args.comments
      (pos, c) <- m
      h <- gg.getHistoryHash(GamePosition(b, pos))
    } yield h -> c
    gg.copy(newComments = comments)
  }

}

