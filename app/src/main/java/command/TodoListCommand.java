package command;

import vo.Todo;

import vo.CalendarPrint;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class TodoListCommand {
    private List<Todo> todos;
    private static final String FILE_NAME = "todos.dat";

    public TodoListCommand() {
        todos = new ArrayList<>();
        loadTodosFromFile();

    }

    public void addTodo() {
        Scanner scanner = new Scanner(System.in);
        CalendarPrint calendarPrint = new CalendarPrint();

        System.out.println("===== Todo 추가 =====");
        calendarPrint.printCalendar();
        System.out.print("할 일을 입력하세요: ");
        String title = scanner.nextLine();

        System.out.print("시작일을 입력하세요 (yyyy-mm-dd, 엔터를 누르면 오늘 날짜): ");
        String startDateStr = scanner.nextLine();
        LocalDate startDate = startDateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(startDateStr);

        System.out.print("마감일을 입력하세요 (yyyy-mm-dd, 엔터를 누르면 오늘 날짜): ");
        String endDateStr = scanner.nextLine();
        LocalDate endDate = endDateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(endDateStr);

        System.out.print("태그를 입력하세요 (쉼표로 구분): ");
        String[] tagArray = scanner.nextLine().split(",");
        Set<String> tags = new HashSet<>();
        for (String tag : tagArray) {
            tags.add(tag.trim());
        }

        Todo todo = new Todo(Todo.getNextSeqNo(), title, startDate, endDate, tags);
        todos.add(todo);
        saveTodosToFile();

        System.out.println("Todo가 추가되었습니다.");
    }

    public void completeTodo() {
        displayIncompleteTodos();
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== Todo 완료 체크 =====");
        System.out.print("완료할 Todo의 번호를 입력하세요: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Todo todo = findTodoByNo(id);
        if (todo != null) {
            todo.setCompleted(true);
            saveTodosToFile();
            System.out.println("Todo가 완료 처리되었습니다.");
        } else {
            System.out.println("해당 번호의 Todo를 찾을 수 없습니다.");
        }
    }

    public void updateTodo() {
        Scanner scanner = new Scanner(System.in);
        displayAllTodos();

        System.out.println("===== Todo 수정 =====");
        System.out.print("수정할 Todo의 번호를 입력하세요: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Todo todo = findTodoByNo(id);
        if (todo != null) {
            System.out.println("현재 Todo 정보: " + todo);
            System.out.print("새로운 할 일을 입력하세요 (변경하지 않으려면 엔터): ");
            String newTitle = scanner.nextLine();
            if (!newTitle.isEmpty()) {
                todo.setTitle(newTitle);
            }

            System.out.print("새로운 시작일을 입력하세요 (yyyy-mm-dd, 변경하지 않으려면 엔터): ");
            String newStartDateStr = scanner.nextLine();
            if (!newStartDateStr.isEmpty()) {
                LocalDate newStartDate = LocalDate.parse(newStartDateStr);
                todo.setStartDate(newStartDate);
            }

            System.out.print("새로운 마감일을 입력하세요 (yyyy-mm-dd, 변경하지 않으려면 엔터): ");
            String newEndDateStr = scanner.nextLine();
            if (!newEndDateStr.isEmpty()) {
                LocalDate newEndDate = LocalDate.parse(newEndDateStr);
                todo.setEndDate(newEndDate);
            }

            System.out.print("새로운 태그를 입력하세요 (쉼표로 구분, 변경하지 않으려면 엔터): ");
            String newTagsStr = scanner.nextLine();
            if (!newTagsStr.isEmpty()) {
                String[] tagArray = newTagsStr.split(",");
                Set<String> newTags = new HashSet<>();
                for (String tag : tagArray) {
                    newTags.add(tag.trim());
                }
                todo.setTags(newTags);
            }

            saveTodosToFile();
            System.out.println("Todo가 수정되었습니다.");
        } else {
            System.out.println("해당 번호의 Todo를 찾을 수 없습니다.");
        }
    }

    public void deleteTodo() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== Todo 삭제 =====");
        System.out.print("삭제할 Todo의 번호를 입력하세요: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Todo todo = findTodoByNo(id);
        if (todo != null) {
            todos.remove(todo);
            saveTodosToFile();
            System.out.println("Todo가 삭제되었습니다.");
        } else {
            System.out.println("해당 번호의 Todo를 찾을 수 없습니다.");
        }
    }

    public void displayTodos() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== Todo 조회 =====");
        System.out.println("1. 전체 목록");
        System.out.println("2. 완료된 항목 목록");
        System.out.println("3. 미완료 항목 목록");
        System.out.println("4. 날짜별 조회");
        System.out.println("5. 태그별 조회");
        System.out.println("0. 뒤로 가기");
        System.out.print("메뉴를 선택하세요: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                displayAllTodos();
                break;
            case 2:
                displayCompletedTodos();
                break;
            case 3:
                displayIncompleteTodos();
                break;
            case 4:
                displayTodosByDate();
                break;
            case 5:
                displayTodosByTag();
                break;
            case 0:
                return;
            default:
                System.out.println("잘못된 선택입니다. 0에서 5 사이의 숫자를 입력하세요.");
                break;
        }
    }

    public void displayAllTodos() {
        System.out.println("===== 전체 Todo 목록 =====");
        for (Todo todo : todos) {
            System.out.println(todo);
        }
    }

    public void displayCompletedTodos() {
        System.out.println("===== 완료된 Todo 목록 =====");
        for (Todo todo : todos) {
            if (todo.isCompleted()) {
                System.out.println(todo);
            }
        }
    }

    public void displayIncompleteTodos() {
        System.out.println("===== 미완료 Todo 목록 =====");
        for (Todo todo : todos) {
            if (!todo.isCompleted()) {
                System.out.println(todo);
            }
        }
    }

    public void displayTodosByDate() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("조회할 날짜를 입력하세요 (yyyy-mm-dd): ");
        String dateStr = scanner.nextLine();
        LocalDate date = LocalDate.parse(dateStr);

        System.out.println("===== " + date + "의 Todo 목록 =====");
        for (Todo todo : todos) {
            if (todo.getEndDate().isEqual(date)) {
                System.out.println(todo);
            }
        }
    }

    public void displayTodosByTag() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("조회할 태그를 입력하세요: ");
        String tag = scanner.nextLine();

        System.out.println("===== 태그: " + tag + "의 Todo 목록 =====");
        for (Todo todo : todos) {
            if (todo.getTags().contains(tag)) {
                System.out.println(todo);
            }
        }
    }

    public Todo findTodoByNo(int no) {
        for (Todo todo : todos) {
            if (todo.getNo() == no) {
                return todo;
            }
        }
        return null;
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
