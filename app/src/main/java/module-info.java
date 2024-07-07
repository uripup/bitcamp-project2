@SuppressWarnings("module")
module bitcamp.project2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    opens bitcamp.project2 to javafx.fxml;
    opens bitcamp.project2.vo to javafx.base;

    exports bitcamp.project2;
}