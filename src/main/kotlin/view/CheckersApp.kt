package view

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import tornadofx.App

class CheckersApp: App(CheckersAppView::class) {
    override fun start(stage: Stage) {
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/mainScene.fxml"))
        stage.title = "Checkers"
        stage.scene = Scene(root)
        stage.show()
    }

}

fun main(args: Array<String>) {
    Application.launch(CheckersApp::class.java, *args)
}

