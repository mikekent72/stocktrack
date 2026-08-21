package com.mikekent.stocktrack;

import com.mikekent.stocktrack.database.DatabaseInitialiser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        DatabaseInitialiser.initialise();

        Label label = new Label("StockTrack");

        Scene scene = new Scene(new StackPane(label), 800, 500);

        stage.setTitle("StockTrack");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}