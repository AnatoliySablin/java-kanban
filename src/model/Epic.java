package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskId = new ArrayList<>();
    private TaskType taskType;

    public Epic(String task, String description) {

        super(task, description);
        taskType = TaskType.EPIC;
    }

    public Epic(String task, String description, int id, Status status) {
        super(task, description, id, status);
        taskType = TaskType.EPIC;
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void addSubtaskId(int code) {
        subtaskId.add(code);
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status + '\'' +
                ", subtaskId=" + subtaskId +
                '}';
    }
}
