package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String task, String description, Status status, int epicId) {
        super(task, description);
        this.epicId = epicId;
        this.status = status;
    }

    public Subtask(String task, String description, int id, Status status, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(task, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String task, String description, Status status, LocalDateTime startTime, Duration duration,
                   int epicId) {
        super(task, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {

        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status + '\'' +
                ", LocalDateTime=" + startTime +
                ", Duration=" + duration +
                ", epicId=" + epicId +
                '}';
    }
}
