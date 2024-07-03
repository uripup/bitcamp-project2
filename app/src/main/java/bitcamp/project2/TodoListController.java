package bitcamp.project2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import bitcamp.project2.vo.Todo;
import bitcamp.project2.command.TodoListCommand;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TodoListController {

    @FXML
    private ListView<Todo> todoListView;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TextField searchTextField;

    private TodoListCommand todoListCommand;
    private ObservableList<Todo> todoObservableList;

    public void initialize() {
        todoListCommand = new TodoListCommand();
        todoObservableList = FXCollections.observableArrayList(todoListCommand.getTodos());
        todoListView.setItems(todoObservableList);

        filterComboBox.getItems().addAll("모두", "완료", "미완료");
        filterComboBox.setValue("모두");
        filterComboBox.setOnAction(event -> filterTodos());

        refreshTodoList();
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
    private void completeTodo() {
            Todo selectedTodo = todoListView.getSelectionModel().getSelectedItem();
            if (selectedTodo != null) {
                selectedTodo.setCompleted(!selectedTodo.isCompleted());
                refreshTodoList();
                todoListCommand.saveTodosToFile();
        }
    }

    @FXML
    private void updateTodo() {
        Todo selectedTodo = todoListView.getSelectionModel().getSelectedItem();
        if (selectedTodo != null) {
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
        Todo selectedTodo = todoListView.getSelectionModel().getSelectedItem();
        if (selectedTodo != null) {
            todoListCommand.getTodos().remove(selectedTodo);
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
        todoObservableList.clear();
        for (Todo todo : todoListCommand.getTodos()) {
            if (filter.equals("모두") ||
                    (filter.equals("완료") && todo.isCompleted()) ||
                    (filter.equals("미완료") && !todo.isCompleted())) {
                todoObservableList.add(todo);
            }
        }
    }

    private void refreshTodoList() {
        todoObservableList.clear();
        todoObservableList.addAll(todoListCommand.getTodos());
    }
}