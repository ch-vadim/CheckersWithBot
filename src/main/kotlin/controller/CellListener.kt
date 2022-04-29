package controller

import model.Cell

interface CellListener {
    fun cellClicked(cell: Cell)
}