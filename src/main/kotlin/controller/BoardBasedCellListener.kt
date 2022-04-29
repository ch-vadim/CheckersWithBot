package controller

import model.Board
import model.Cell

class BoardBasedCellListener(private val board: Board) : CellListener {
    override fun cellClicked(cell: Cell) {
        board.makeTurn(cell)
    }
}