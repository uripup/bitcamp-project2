@SuppressWarnings("module")
module bitcamp.project2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens bitcamp.project2 to javafx.fxml;
    exports bitcamp.project2;
}