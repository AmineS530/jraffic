package Jraffic;

import Jraffic.graphics.MainPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static final Double WIDTH = 1440.0;
    private static final Double HEIGHT = 900.0;

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new MainPane(), WIDTH, HEIGHT);

        stage.setTitle("Jraffic");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}