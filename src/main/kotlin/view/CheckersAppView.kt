package view

import controller.BoardBasedCellListener
import controller.BoardListener
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import model.*
import tornadofx.*

class CheckersAppView: View(), BoardListener {
    companion object {
        const val columns = 8
        const val rows = 8
        var inProcess = true
        var white = true //белые - человек
        var black = true //черные - человек
        var needToClose = false
        var statusText = ""
        var currentPlayer = CheckerColor.BLACK
    }

    private lateinit var status: Label
    private val board = Board(columns, rows)


    private val buttons = mutableMapOf<Cell, Button>()
    override val root = BorderPane()
    private var listener: BoardBasedCellListener

    //создание сцены игры
    init {
        title = "Checkers"
        listener = BoardBasedCellListener(board)
        board.setListener(this)
        board.setComputerPlayer(!black, !white)
        with (root) {
            top{
                vbox{
                    menubar {
                        menu("Menu") {
                            item("Restart").action {
                                reconfigurateGame()
                            }
                            item("Exit").action {
                                this@CheckersAppView.close()
                            }
                        }
                    }
                }
            }
            center {
                gridpane {
                    for (y in 0 until rows) {
                        row {
                            for (x in 0 until columns) {
                                val cell = Cell(x, y)
                                val button = button (graphic = ImageView(getImage(x, y))) {
                                    style {
                                        minWidth = 48.px
                                        minHeight = 48.px
                                        padding = box(0.px)
                                    }
                                }
                                button.action {
                                    if (inProcess) {
                                        listener.cellClicked(cell)
                                    }
                                }
                                buttons[cell] = button
                            }
                        }
                    }
                }
            }
            bottom {
                status = label("")
            }

        }
        statusText = when {
            !black -> ("PVE (White): move")
            !white -> ("PVE (Black): move")
            else -> ("PVP: move")
        }
        updateBoardAndStatus()
        if(!black) board.computerMove()
    }
    //изображение для клетки
    private fun getImage(x:Int, y:Int): String {
        return when {
            board[x, y] is Queen && board[x, y]?.color == CheckerColor.BLACK -> "blackCheckerQueen.png"
            board[x, y] is Queen && board[x, y]?.color == CheckerColor.WHITE -> "whiteCheckerQueen.png"
            board[x, y]?.color == CheckerColor.BLACK -> "/blackChecker.png"
            board[x, y]?.color == CheckerColor.WHITE -> "/whiteChecker.png"
            (y+x)%2 == 1 -> "/blackCell.png"
            else -> "/whiteCell.png"

        }
    }
    //перезапуск игры
    private fun reconfigurateGame() {
        inProcess = false
        val stage = Stage()
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/restartScene.fxml"))
        stage.title = "Checkers"
        stage.scene = Scene(root)
        stage.showAndWait()
        if(needToClose) this@CheckersAppView.close()
        if (inProcess) restartGame()

    }
    //обновление игрового поля
    private fun updateBoardAndStatus(cells: List<Cell> = listOf()) {
        currentPlayer = board.currentPlayer
        status.text = if (currentPlayer == CheckerColor.BLACK) "$statusText PURPLE" else "$statusText WHITE"
        for (cell in cells) {
            val imageName = getImage(cell.x, cell.y)
            buttons[cell]?.apply {
                graphic = ImageView(imageName)
                style {
                    minHeight = 48.px
                    minWidth = 48.px
                    padding = box(0.px)
                }
            }
        }

        val winner = board.checkWinner()
        if (winner != null) {
            status.text = "$winner win!"
            inProcess = false
        }

    }

    private fun restartGame() {
        board.clear()
        for (x in 0 until  columns) {
            for (y in 0 until rows) {
                updateBoardAndStatus(listOf(Cell(x, y)))
            }
        }
        inProcess = true
        board.setComputerPlayer(!black, !white)
        if(!black) board.computerMove()

    }
    //ход выполнен
    override fun turnMade(cells: MutableList<Cell> ) {
        updateBoardAndStatus(cells)
    }

}