package com.technologicky_andrea.hexagonifier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HexagonifyImage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/hexagonifier_main.fxml"));
        HBox root = loader.load();

        primaryStage.setTitle("Hexagonifier");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
