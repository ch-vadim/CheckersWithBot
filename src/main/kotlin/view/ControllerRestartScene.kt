package view

import javafx.fxml.FXML
import javafx.scene.control.Button


class ControllerRestartScene {

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
            CheckersAppView.needToClose = true

        }
        //запуск в режиме PVE (Black)
        buttonPveBlack.setOnAction {
            buttonExit.scene.window.hide()
            CheckersAppView.white = false
            CheckersAppView.black = true
            CheckersAppView.inProcess = true

        }
        //запуск в режиме PVE (White)
        buttonPveWhite.setOnAction {
            buttonExit.scene.window.hide()
            CheckersAppView.black = false
            CheckersAppView.white = true
            CheckersAppView.inProcess = true


        }
        //запуск в режиме PVP
        buttonPvp.setOnAction {
            buttonExit.scene.window.hide()
            CheckersAppView.black = true
            CheckersAppView.white = true
            CheckersAppView.inProcess = true


        }
    }
}
