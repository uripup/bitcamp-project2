package bitcamp.project2.command;

import bitcamp.project2.vo.Todo;

import java.io.*;
import java.util.*;

public class TodoListCommand {
    private List<Todo> todos;
    private static final String FILE_NAME = "todos.dat";

    public TodoListCommand() {
        todos = new ArrayList<>();
        loadTodosFromFile();
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public void saveTodosToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(todos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadTodosFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            todos = (List<Todo>) ois.readObject();
            if (todos.isEmpty()) {
                Todo.setSeqNo(0);
            } else {
                Todo.setSeqNo(todos.get(todos.size() - 1).getNo());
            }
        } catch (IOException | ClassNotFoundException e) {
            todos = new ArrayList<>();
        }
    }
}