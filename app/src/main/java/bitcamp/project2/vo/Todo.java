package bitcamp.project2.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class Todo implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int seqNo = 0;
    private int no;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<String> tags;
    private boolean completed;

    public Todo(int no, String title, LocalDate startDate, LocalDate endDate, Set<String> tags) {
        this.no = no;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tags = tags;
        this.completed = false;
    }

    public static int getNextSeqNo() {
        return ++seqNo;
    }

    public static void setSeqNo(int seqNo) {
        Todo.seqNo = seqNo;
    }

    // Getter 및 Setter 메소드들
    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        String status = completed ? "[완료]" : "[미완료]";
        return no + ". " + status + " " + title + " (시작일: " + startDate + ", 마감일: " + endDate + ")";
    }
}