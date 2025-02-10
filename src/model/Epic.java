package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(String task, String description) {

        super(task, description);
    }

    public Epic(String task, String description, int id, Status status, ArrayList<Integer> subtaskCodes) {
        super(task, description, id, status);
    }

    public ArrayList<Integer> getSubtaskId() {

        return subtaskId;
    }

    public void addSubtaskId(int code) {
        subtaskId.add(code);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status + '\'' +
                ", subtaskCodes=" + subtaskId +
                '}';
    }
}
