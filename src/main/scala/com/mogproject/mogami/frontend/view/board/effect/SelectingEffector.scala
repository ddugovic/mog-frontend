package com.mogproject.mogami.frontend.view.board.effect

import com.mogproject.mogami.frontend.view.board.SVGBoard

/**
  * Circular selecting effect
  */
case class SelectingEffector(svgBoard: SVGBoard) extends CircularEffectorLike {
  override val duration = "3s"

  override val finalRadius: Int = SVGBoard.PIECE_WIDTH * 4

  override val opacityValues: Seq[Double] = Seq(1, 0, 0, 0)
}