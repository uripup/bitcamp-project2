package bitcamp.project2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseButton;
import java.time.temporal.ChronoUnit;

import bitcamp.project2.vo.Todo;
import bitcamp.project2.command.TodoListCommand;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TodoListController {

    @FXML
    private TableView<Todo> todoTableView;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableColumn<Todo, Boolean> completedColumn;
    @FXML
    private TableColumn<Todo, String> titleColumn;
    @FXML
    private TableColumn<Todo, LocalDate> startDateColumn;
    @FXML
    private TableColumn<Todo, LocalDate> endDateColumn;
    @FXML
    private TableColumn<Todo, String> tagsColumn;

    private TodoListCommand todoListCommand;
    private ObservableList<Todo> todoObservableList;

    public void initialize() {
        todoListCommand = new TodoListCommand();
        todoObservableList = FXCollections.observableArrayList(todoListCommand.getTodos());
        todoTableView.setItems(todoObservableList);

        todoTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        filterComboBox.getItems().addAll("모두", "완료", "미완료");
        filterComboBox.setValue("모두");
        filterComboBox.setOnAction(event -> filterTodos());

        completedColumn.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
        completedColumn.setCellFactory(column -> new CheckBoxTableCell<>());
        completedColumn.setEditable(true);

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setCellFactory(column -> new TableCell<Todo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Todo todo = getTableView().getItems().get(getIndex());
                    if (todo.isCompleted()) {
                        setStyle("-fx-text-fill: purple;");
                    } else {
                        long daysBetween = ChronoUnit.DAYS.between(todo.getStartDate(), todo.getEndDate());
                        if (daysBetween <= 1) {
                            setStyle("-fx-text-fill: red;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            }
        });

        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        tagsColumn.setCellValueFactory(cellData -> {
            String tags = String.join(", ", cellData.getValue().getTags());
            return new SimpleStringProperty(tags);
        });

        todoTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                showTodoDetails(todoTableView.getSelectionModel().getSelectedItem());
            } else if (event.getClickCount() == 1 && event.getButton() == MouseButton.PRIMARY) {
                if (!todoTableView.getSelectionModel().isEmpty()) {
                    TablePosition pos = todoTableView.getSelectionModel().getSelectedCells().get(0);
                    if (pos != null && pos.getColumn() == completedColumn.getTableView().getColumns().indexOf(completedColumn)) {
                        Todo todo = todoTableView.getItems().get(pos.getRow());
                        todo.setCompleted(!todo.isCompleted());
                        todoListCommand.saveTodosToFile();
                        filterTodos();
                        todoTableView.refresh();
                    }
                }
            }
        });
        todoTableView.setEditable(true);
        todoTableView.getSelectionModel().setCellSelectionEnabled(true);

        todoTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleMultipleSelection();
            }
        });

        refreshTodoList();
    }

    private void handleMultipleSelection() {
        ObservableList<Todo> selectedItems = todoTableView.getSelectionModel().getSelectedItems();
        System.out.println("선택된 항목 수: " + selectedItems.size());
    }

    @FXML
    private void addTodo() {
        Dialog<Todo> dialog = new Dialog<>();
        dialog.setTitle("Todo 추가");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField titleField = new TextField();
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now());
        TextField tagsField = new TextField();

        dialogPane.setContent(new VBox(8,
                new Label("제목:"), titleField,
                new Label("시작일:"), startDatePicker,
                new Label("마감일:"), endDatePicker,
                new Label("태그 (쉼표로 구분):"), tagsField));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String title = titleField.getText();
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                Set<String> tags = new HashSet<>();
                for (String tag : tagsField.getText().split(",")) {
                    tags.add(tag.trim());
                }
                return new Todo(Todo.getNextSeqNo(), title, startDate, endDate, tags);
            }
            return null;
        });

        Optional<Todo> result = dialog.showAndWait();
        result.ifPresent(todo -> {
            todoListCommand.getTodos().add(todo);
            refreshTodoList();
            todoListCommand.saveTodosToFile();
        });
    }

    @FXML
    private void updateTodo() {
        ObservableList<Todo> selectedItems = todoTableView.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            Todo selectedTodo = selectedItems.get(0);
            Dialog<Todo> dialog = new Dialog<>();
            dialog.setTitle("Todo 수정");

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            TextField titleField = new TextField(selectedTodo.getTitle());
            DatePicker startDatePicker = new DatePicker(selectedTodo.getStartDate());
            DatePicker endDatePicker = new DatePicker(selectedTodo.getEndDate());
            TextField tagsField = new TextField(String.join(", ", selectedTodo.getTags()));

            dialogPane.setContent(new VBox(8,
                    new Label("제목:"), titleField,
                    new Label("시작일:"), startDatePicker,
                    new Label("마감일:"), endDatePicker,
                    new Label("태그 (쉼표로 구분):"), tagsField));

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    selectedTodo.setTitle(titleField.getText());
                    selectedTodo.setStartDate(startDatePicker.getValue());
                    selectedTodo.setEndDate(endDatePicker.getValue());
                    Set<String> tags = new HashSet<>();
                    for (String tag : tagsField.getText().split(",")) {
                        tags.add(tag.trim());
                    }
                    selectedTodo.setTags(tags);
                    return selectedTodo;
                }
                return null;
            });

            Optional<Todo> result = dialog.showAndWait();
            result.ifPresent(todo -> {
                refreshTodoList();
                todoListCommand.saveTodosToFile();
            });
        }
    }

    @FXML
    private void deleteTodo() {
        ObservableList<Todo> selectedItems = todoTableView.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            todoListCommand.getTodos().removeAll(selectedItems);
            refreshTodoList();
            todoListCommand.saveTodosToFile();
        }
    }

    @FXML
    private void searchTodos() {
        String searchText = searchTextField.getText().toLowerCase();
        todoObservableList.clear();
        for (Todo todo : todoListCommand.getTodos()) {
            if (todo.getTitle().toLowerCase().contains(searchText) ||
                    todo.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(searchText))) {
                todoObservableList.add(todo);
            }
        }
    }

    private void filterTodos() {
        String filter = filterComboBox.getValue();
        System.out.println("Todo 목록 필터링 중. 현재 필터: " + filter);
        todoObservableList.clear();
        for (Todo todo : todoListCommand.getTodos()) {
            if (filter.equals("모두") ||
                    (filter.equals("완료") && todo.isCompleted()) ||
                    (filter.equals("미완료") && !todo.isCompleted())) {
                todoObservableList.add(todo);
                System.out.println("필터링된 목록에 추가됨: " + todo.getTitle() + ", 완료 여부: " + (todo.isCompleted() ? "완료" : "미완료"));
            }
        }
        System.out.println("필터링된 Todo 항목 수: " + todoObservableList.size());
    }


    private void refreshTodoList() {
        todoObservableList.clear();
        todoObservableList.addAll(todoListCommand.getTodos());
    }

    private void showTodoDetails(Todo todo) {
        if (todo != null) {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Todo 상세 정보");

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().add(ButtonType.CLOSE);

            VBox content = new VBox(10);
            content.getChildren().addAll(
                    new Label("제목: " + todo.getTitle()),
                    new Label("시작일: " + todo.getStartDate()),
                    new Label("마감일: " + todo.getEndDate()),
                    new Label("태그: " + String.join(", ", todo.getTags())),
                    new Label("상태: " + (todo.isCompleted() ? "완료" : "미완료"))
            );

            dialogPane.setContent(content);
            dialog.showAndWait();
        }
    }
}
