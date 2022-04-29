package view

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.stage.Stage
import tornadofx.App
import java.net.URL
import java.util.*


class ControllerMainScene {
    @FXML
    private lateinit var resources: ResourceBundle

    @FXML
    private lateinit var location: URL

    @FXML
    private lateinit var buttonPveBlack: Button

    @FXML
    private lateinit var buttonPveWhite: Button

    @FXML
    private lateinit var buttonPvp: Button

    @FXML
    private lateinit var buttonExit: Button
    @FXML
    fun initialize() {
        //выход
        buttonExit.setOnAction {
            buttonExit.scene.window.hide()

        }
        //запуск в режиме PVE (Black)
        buttonPveBlack.setOnAction {
            buttonExit.scene.window.hide()
            CheckersAppView.white = false
            val stage = Stage()
            App(CheckersAppView::class).start(stage)
        }
        //запуск в режиме PVE (White)
        buttonPveWhite.setOnAction {
            buttonExit.scene.window.hide()
            CheckersAppView.black = false
            val stage = Stage()
            App(CheckersAppView::class).start(stage)

        }
        //запуск в режиме PVP
        buttonPvp.setOnAction {
            buttonExit.scene.window.hide()
            val stage = Stage()
            App(CheckersAppView::class).start(stage)

        }
    }
}
