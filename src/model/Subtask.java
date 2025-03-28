package model;

public class Subtask extends Task {
    private int epicId;
    private TaskType taskType;

    public Subtask(String task, String description, int epicId) {
        super(task, description);
        this.epicId = epicId;
        taskType = TaskType.SUBTASK;
    }

    public Subtask(String task, String description, int id, Status status, int epicId) {
        super(task, description, id, status);
        this.epicId = epicId;
        taskType = TaskType.SUBTASK;
    }

    public Subtask(String task, String description, Status status) {
        super(task, description, status);
    }

    public int getEpicId() {

        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public TaskType getTaskType() {
        return taskType;
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
