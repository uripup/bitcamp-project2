package bitcamp.project2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TodoApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/TodoList.fxml"));
        primaryStage.setTitle("Todo List");
        primaryStage.setScene(new Scene(root, 380, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}