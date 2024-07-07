package bitcamp.project2.vo;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;
import javafx.collections.FXCollections;

import javafx.beans.property.*;

public class Todo implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int seqNo = 0;
    private int no;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<String> tags;
    private boolean completed;

    private transient StringProperty titleProperty;
    private transient ObjectProperty<LocalDate> startDateProperty;
    private transient ObjectProperty<LocalDate> endDateProperty;
    private transient SetProperty<String> tagsProperty;
    private transient BooleanProperty completedProperty;

    public Todo(int no, String title, LocalDate startDate, LocalDate endDate, Set<String> tags) {
        this.no = no;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tags = new HashSet<>(tags);
        this.completed = false;
    }

    public static int getNextSeqNo() {
        return ++seqNo;
    }

    public static void setSeqNo(int seqNo) {
        Todo.seqNo = seqNo;
    }

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
        if (titleProperty != null) {
            titleProperty.set(title);
        }
    }

    public StringProperty titleProperty() {
        if (titleProperty == null) {
            titleProperty = new SimpleStringProperty(title);
        }
        return titleProperty;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        if (startDateProperty != null) {
            startDateProperty.set(startDate);
        }
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        if (startDateProperty == null) {
            startDateProperty = new SimpleObjectProperty<>(startDate);
        }
        return startDateProperty;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        if (endDateProperty != null) {
            endDateProperty.set(endDate);
        }
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        if (endDateProperty == null) {
            endDateProperty = new SimpleObjectProperty<>(endDate);
        }
        return endDateProperty;
    }

    public Set<String> getTags() {
        return new HashSet<>(tags);
    }

    public void setTags(Set<String> tags) {
        this.tags = new HashSet<>(tags);
        if (tagsProperty != null) {
            tagsProperty.set(FXCollections.observableSet(this.tags));
        }
    }

    public SetProperty<String> tagsProperty() {
        if (tagsProperty == null) {
            tagsProperty = new SimpleSetProperty<>(FXCollections.observableSet(tags));
        }
        return tagsProperty;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completedProperty != null) {
            completedProperty.set(completed);
        }
    }

    public BooleanProperty completedProperty() {
        if (completedProperty == null) {
            completedProperty = new SimpleBooleanProperty(completed);
        }
        return completedProperty;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    @Override
    public String toString() {
        String status = completed ? "[완료]" : "[미완료]";
        return no + ". " + status + " " + title + " (시작일: " + startDate + ", 마감일: " + endDate + ")";
    }
}