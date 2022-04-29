package model

class Queen(color: CheckerColor): Checker(color) {
    //возможные ходы для шашки бота
    override fun getPossibleMovesNoEvent(cell: Cell, checkers: Map<Cell, Checker?>): List<Cell> {
        setTempBoard(checkers)
        val result = getPossibleMoves(cell)
        setTempBoard(null)
        return result
    }
    //должна ли шашка бота атаковать
    override fun isShouldAttackNoEvent(cell: Cell, checkers: Map<Cell, Checker?>): Boolean {
        setTempBoard(checkers)
        val result = isShouldAttack(cell)
        setTempBoard(null)
        return result
    }
    //возможные ходы
    override fun getPossibleMoves(cell: Cell): List<Cell> {
        val checkers = getCheckers()!!
        val result = mutableListOf(cell)
        val eatList = mutableListOf(cell)
        for (direction in DIRECTIONS) {
            val newCell = cell + direction
            if (inDesk(newCell)) {
                val nextCell = newCell + direction
                if (isOpposite(checkers[newCell]) &&
                    inDesk(nextCell) && checkers[nextCell] == null) {
                    eatList.add(nextCell)
                    continue
                }
                if (checkers[newCell] == null)
                    result.add(newCell)
            }
        }

        return if (eatList.size > 1) eatList else result

    }
    //должна ли шашка атаковать
    override fun isShouldAttack(cell: Cell): Boolean {
        val checkers = getCheckers()!!
        for (direction in DIRECTIONS) {
            val newCell = cell + direction
            if (inDesk(newCell)) {
                val nextCell = newCell + direction
                if (isOpposite(checkers[newCell]) &&
                        inDesk(nextCell) && checkers[nextCell] == null) return true
            }
        }
        return false

    }

}