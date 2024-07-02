import command.TodoListCommand;
import vo.Todo;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Scanner;

public class App {
    private TodoListCommand todoListCommand = new TodoListCommand();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayTodayTodos();

            System.out.println("1. TodoList 시작하기");
            System.out.println("0. 종료");
            System.out.print("메뉴를 선택하세요: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    todoListMenu();
                    break;
                case 0:
                    System.out.println("프로그램을 종료합니다.");
                    todoListCommand.saveTodosToFile();
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 선택해주세요.");
                    break;
            }
        }
    }

    private void todoListMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===== vo.Todo 목록 메뉴 =====");
            System.out.println("1. 추가");
            System.out.println("2. 완료 체크");
            System.out.println("3. 조회");
            System.out.println("4. 수정");
            System.out.println("5. 삭제");
            System.out.println("0. 뒤로 가기");
            System.out.print("메뉴를 선택하세요: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    todoListCommand.addTodo();
                    break;
                case 2:
                    todoListCommand.completeTodo();
                    break;
                case 3:
                    todoListCommand.displayTodos();
                    break;
                case 4:
                    todoListCommand.updateTodo();
                    break;
                case 5:
                    todoListCommand.deleteTodo();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 선택해주세요.");
                    break;
            }
        }
    }

    private void displayTodayTodos() {
        LocalDate today = LocalDate.now();

        System.out.println("===== 오늘 할 일 =====");
        todoListCommand.getTodos().stream()
                .filter(todo -> !todo.isCompleted() && todo.getEndDate().isEqual(today))
                .sorted(Comparator.comparingInt(Todo::getNo))
                .forEach(todo -> {
                    System.out.println(todo);
                    System.out.println("=================");
                });
    }
}
