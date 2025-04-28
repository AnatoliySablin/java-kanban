package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String task, String description) {

        super(task, description);
    }

    public Epic(String task, String description, int id, Status status) {
        super(task, description, id, status, null, null);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void addSubtaskId(int code) {
        subtaskId.add(code);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status + '\'' +
                ", starttime=" + startTime +
                ", duration=" + duration +
                ", subtaskId=" + subtaskId +
                '}';
    }
}
