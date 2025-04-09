package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String task, String description, int epicId) {
        super(task, description);
        this.epicId = epicId;
    }

    public Subtask(String task, String description, int id, Status status, int epicId) {
        super(task, description, id, status);
        this.epicId = epicId;
    }

    public Subtask(String task, String description, Status status, int epicId) {
        super(task, description, status);
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
                ", epicId=" + epicId +
                '}';
    }
}
