package model


open class Checker(val color: CheckerColor) {
    companion object {
        val DIRECTIONS = arrayOf(
            Cell(1, 1), Cell(-1, 1),
            Cell(1, -1), Cell(-1, -1)
        )
    }

    private var mainBoard: Board? = null

    private var tempBoard: Map<Cell, Checker?>? = null

    fun setBoard(board: Board) {
        this.mainBoard = board
    }

    fun setTempBoard(map: Map<Cell, Checker?>?) {
        this.tempBoard = map
    }

    fun getCheckers() = if (tempBoard!= null) tempBoard else mainBoard!!.getAllCheckers()

    fun inDesk(cell: Cell) = cell.x in 0..7 && cell.y in 0..7
    //цвет шашки противника
    fun isOpposite(other: Checker?): Boolean {
        if (other != null && other.color != this.color) {
            return true
        }
        return false
    }
    //возможные ходы для шашки бота
    open fun getPossibleMovesNoEvent(cell: Cell, checkers: Map<Cell, Checker?>): List<Cell> {
        tempBoard = checkers
        val result = getPossibleMoves(cell)
        tempBoard = null
        return result
    }
    //должна ли шашка бота атаковать
    open fun isShouldAttackNoEvent(cell: Cell, checkers: Map<Cell, Checker?>): Boolean {
        tempBoard = checkers
        val result = isShouldAttack(cell)
        tempBoard = null
        return result
    }
    //возможные ходы
    open fun getPossibleMoves(cell: Cell): List<Cell> {
        val checkers = getCheckers()!!
        val result = mutableListOf<Cell>()
        val eatList = mutableListOf<Cell>()
        if (color == CheckerColor.BLACK ) {
            for (i in 0..1) {
                val newCell = cell + DIRECTIONS[i]
                if (inDesk(newCell)) {
                    val nextCell = newCell + DIRECTIONS[i]
                    if (isOpposite(checkers[newCell]) &&
                            inDesk(nextCell) && checkers[nextCell] == null) {
                                eatList.add(nextCell)
                                continue
                    }
                    if (checkers[newCell] == null) result.add(newCell)
                }
            }
        }
        if (color == CheckerColor.WHITE) {
            for (i in 2..3) {
                val newCell = cell + DIRECTIONS[i]
                if (inDesk(newCell)) {
                    val nextCell = newCell + DIRECTIONS[i]
                    if (isOpposite(checkers[newCell]) &&
                        inDesk(nextCell) && checkers[nextCell] == null) {
                        eatList.add(nextCell)
                        continue
                    }
                    if (checkers[newCell] == null) result.add(newCell)
                }
            }
        }
        return if (eatList.isNotEmpty()) eatList else result
    }
    //должна ли шашка атаковать
    open fun isShouldAttack(cell: Cell): Boolean {
        val checkers = getCheckers()!!
        if (color == CheckerColor.BLACK ) {
            for (i in 0..1) {
                val newCell = cell + DIRECTIONS[i]
                if (inDesk(newCell)) {
                    val nextCell = newCell + DIRECTIONS[i]
                    if (isOpposite(checkers[newCell]) &&
                        inDesk(nextCell) && checkers[nextCell] == null) return true
                }
            }
        }
        if (color == CheckerColor.WHITE) {
            for (i in 2..3) {
                val newCell = cell + DIRECTIONS[i]
                if (inDesk(newCell)) {
                    val nextCell = newCell + DIRECTIONS[i]
                    if (isOpposite(checkers[newCell]) &&
                        inDesk(nextCell) && checkers[nextCell] == null) return true
                }
            }
        }
        return false
    }

}