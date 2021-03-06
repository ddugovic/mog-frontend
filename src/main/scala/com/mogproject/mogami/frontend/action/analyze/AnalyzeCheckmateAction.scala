package com.mogproject.mogami.frontend.action.analyze

import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model.{AnalyzeResultMessage, PlaygroundModel}
import com.mogproject.mogami.frontend.model.analyze.CheckmateAnalyzeResult
import com.mogproject.mogami.mate.MateSolver

/**
  *
  */
case class AnalyzeCheckmateAction(timeoutSec: Int) extends PlaygroundAction {
  override def execute(model: PlaygroundModel): Option[PlaygroundModel] = {
    model.mode.getGameControl.map { gc =>
      val result = MateSolver.solve(gc.getDisplayingState, gc.getDisplayingLastMoveTo, timeLimitMillis = 1000L * timeoutSec)
      model.copy(messageBox = Some(AnalyzeResultMessage(CheckmateAnalyzeResult(result))))
    }
  }
}
