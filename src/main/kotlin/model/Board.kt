package model

import controller.BoardListener

class Board constructor(private val width:Int = 8, private val height:Int = 8) {
    private val checkers: MutableMap<Cell, Checker?> = mutableMapOf() //все шашки на доске
    var currentPlayer = CheckerColor.BLACK //текущий игрок
    private var isChoosen: Cell? = null //выбранная для хода шашка
    private var listener: BoardListener? = null
    private var comboAttack  = false //необходимость атаки более одной шашки подряд
    private var blackComputerPlayer: Boolean = false
    private var whiteComputerPlayer: Boolean = false

    //инициализация положения шашек
    init {
        for(x in 0 until width) {
            for (y in 0 until height) {
                when {
                    y<=2 && (y+x)%2 == 1 -> {
                        val checker = Checker(CheckerColor.BLACK)
                        checker.setBoard(this)
                        checkers[Cell(x, y)] = checker
                    }
                    y>=5 && (y+x)%2 == 1 -> {
                        val checker = Checker(CheckerColor.WHITE)
                        checker.setBoard(this)
                        checkers[Cell(x, y)] = checker
                    }
                }
            }
        }
    }
    //опредление игрока-компьютера
    fun setComputerPlayer(black: Boolean, white: Boolean) {
        this.blackComputerPlayer = black
        this.whiteComputerPlayer = white
    }
    fun getAllCheckers(): MutableMap<Cell, Checker?> =  checkers

    operator fun get(x: Int, y: Int): Checker? {
        return get(Cell(x, y))
    }

    operator fun get(cell: Cell): Checker? {
        return checkers[cell]
    }

    //все возможные ходы для шашки
    private fun getPossibleMoves(cell: Cell): List<Cell> {
        return if (checkers[cell] != null) {
            val result = checkers[cell]!!.getPossibleMoves(cell)
            result
        } else emptyList()
    }
    //должна ли шашка атаковать
    private fun isShouldAttack(cell: Cell): Boolean {
        return if (checkers[cell] != null) {
            val result = checkers[cell]!!.isShouldAttack(cell)
            result
        } else false
    }

    //должен ли игрок атаковать
    fun playerShouldAttack(): List<Cell> {
        val result = mutableListOf<Cell>()
        for ((cell, checker) in checkers) {
            if (checker?.color == currentPlayer) {
                if (isShouldAttack(cell))  result.add(cell)
            }
        }
        return result
    }

    fun setListener(boardListener: BoardListener) {
        this.listener = boardListener
    }

    //обработка клика на клетку
    fun makeTurn(cell: Cell) {
        if (checkers[cell] is Checker && currentPlayer == checkers[cell]!!.color && !comboAttack) {
            isChoosen = cell
            return
        }
        val shouldAttack = playerShouldAttack()
        if (isChoosen != null && (shouldAttack.isEmpty() || shouldAttack.contains(isChoosen))) {
            val possibleMoves = getPossibleMoves(isChoosen!!)
            val needAttack = isShouldAttack(isChoosen!!)
            if (possibleMoves.contains(cell)) {
                val cells = mutableListOf<Cell>()
                if (currentPlayer == CheckerColor.BLACK && cell.y == 7 ||
                    currentPlayer == CheckerColor.WHITE && cell.y == 0) {
                        val queen = Queen(currentPlayer)
                        queen.setBoard(this)
                        checkers[cell] = queen
                } else {
                    checkers[cell] = checkers[isChoosen]
                }
                checkers.remove(isChoosen)
                cells.add(isChoosen!!)
                cells.add(cell)
                if (needAttack) {
                    val deathCell = isChoosen!! + (cell - isChoosen!!)/2
                    checkers.remove(deathCell)
                    cells.add(deathCell)
                    if (isShouldAttack(cell)) {
                        isChoosen = cell
                        comboAttack = true
                        listener!!.turnMade(cells)
                        return
                    }

                }
                isChoosen = null
                currentPlayer = currentPlayer.opposite()

                comboAttack = false
                listener!!.turnMade(cells)
                if (currentPlayer == CheckerColor.BLACK && blackComputerPlayer) computerMove()
                if (currentPlayer == CheckerColor.WHITE && whiteComputerPlayer) computerMove()


            }

        }
    }

    //проверка победителя
    fun checkWinner(mapOfCheckers: Map<Cell, Checker?> = checkers): CheckerColor? {
        var checkBlack = false
        var checkWhite = false

        for (checker in mapOfCheckers) {
            if (checker.value?.color == CheckerColor.BLACK) checkBlack = true
            if (checker.value?.color == CheckerColor.WHITE) checkWhite = true
        }

        if (!checkBlack) return CheckerColor.WHITE
        if (!checkWhite) return CheckerColor.BLACK
        return null

    }

    //ход компьютера
    fun computerMove() {
        val list = minMax(2, currentPlayer, checkers )
        val moves = list.moves
        for (move in moves) {
            makeTurn(move)
        }
    }

    //оценка положения на доске
    private fun evaluation (color: CheckerColor = CheckerColor.BLACK, desk: Map<Cell, Checker?>): Int {
        if (color == CheckerColor.WHITE) return -evaluation(CheckerColor.BLACK, desk)
        var result = 0
        for (checker in desk) {
            if (checker.value?.color == CheckerColor.BLACK) {
                result += if (checker.value is Queen) 400 else 100
                if (checker.value?.isShouldAttack(checker.key) == true) result += 700
            }
            if (checker.value?.color == CheckerColor.WHITE) {
                result -= if (checker.value is Queen) 400 else 100
                if (checker.value?.isShouldAttack(checker.key) == true) result -= 700
            }
        }
        return result
    }

    data class EvaluatedTurn(val moves: List<Cell>, val evaluation: Int)

    //минимакс алгоритм для вычисления лучшего хода
    private fun minMax(depth: Int, player: CheckerColor, actualDesk: Map<Cell, Checker?>): EvaluatedTurn {
        when (checkWinner(actualDesk)) {
            currentPlayer -> return EvaluatedTurn(emptyList(), 10000 + depth)
            currentPlayer.opposite() -> EvaluatedTurn(emptyList(), -10000 - depth)
            else -> {}
        }
        if (depth <= 0) return EvaluatedTurn(emptyList(), evaluation(currentPlayer, actualDesk))
        var result = EvaluatedTurn(emptyList(), -100000)
        var possibleChecker = playerShouldAttackNoEvent(player, actualDesk)
        if (possibleChecker.isEmpty()) possibleChecker = actualDesk.keys.toList()
        for (checker in actualDesk) {
            if (checker.value?.color != player) continue
            if (!possibleChecker.contains(checker.key)) continue
            val possibleMoves = checker.value?.getPossibleMovesNoEvent(checker.key, actualDesk) ?: emptyList()
            for (move in possibleMoves) {
                val allMoves = mutableListOf(move)
                val shouldAttack = checker.value!!.isShouldAttackNoEvent(checker.key, actualDesk)
                val newDesk = actualDesk.toMutableMap()
                fakeMove(player, checker.key, move, shouldAttack, newDesk)
                var newMove = move

                while (checker.value!!.isShouldAttackNoEvent(move, newDesk)) {
                    val comboMove = checker.value!!.getPossibleMovesNoEvent(move, newDesk)[0]
                    fakeMove(player, newMove, comboMove, true, newDesk)
                    newMove = comboMove
                    allMoves.add(comboMove)
                }
                val evaluation = -minMax(depth - 1, player.opposite(), newDesk).evaluation
                if (evaluation > result.evaluation) {
                    allMoves.add(0, checker.key)
                    result = EvaluatedTurn(allMoves, evaluation)
                }
            }
        }
        return result
    }
    //должен ли игрок-бот аттаковать
    private fun playerShouldAttackNoEvent(player: CheckerColor, actualDesk: Map<Cell, Checker?>): List<Cell> {
        val result = mutableListOf<Cell>()
        for ((cell, checker) in actualDesk) {
            if (checker?.color == player) {
                if (checker.isShouldAttackNoEvent(cell, actualDesk)) result.add(cell)
            }
        }
        return result
    }
    //иммитация хода
    private fun fakeMove (player: CheckerColor, choosen: Cell, cell: Cell, isAttack: Boolean, desk: MutableMap<Cell, Checker?>) {
        if (player == CheckerColor.BLACK && cell.y == 7 ||
            player == CheckerColor.WHITE && cell.y == 0) {
            val queen = Queen(currentPlayer)
            queen.setBoard(this)
            desk[cell] = queen
        } else {
            desk[cell] = desk[choosen]
        }
        desk.remove(choosen)
        if (isAttack) desk.remove((choosen + cell) / 2)

    }


    //очищение доски
    fun clear() {
        checkers.clear()
        currentPlayer = CheckerColor.BLACK
        isChoosen = null
        for(x in 0 until width) {
            for (y in 0 until height) {
                when {
                    y<=2 && (y+x)%2 == 1 -> {
                        val checker = Checker(CheckerColor.BLACK)
                        checker.setBoard(this)
                        checkers[Cell(x, y)] = checker
                    }
                    y>=5 && (y+x)%2 == 1 -> {
                        val checker = Checker(CheckerColor.WHITE)
                        checker.setBoard(this)
                        checkers[Cell(x, y)] = checker
                    }
                }
            }
        }

    }

}