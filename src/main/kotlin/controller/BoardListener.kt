package controller

import model.Cell

interface BoardListener {
    fun turnMade(cells: MutableList<Cell>)
}