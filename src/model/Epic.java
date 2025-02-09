package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskCodes = new ArrayList<>();

    public Epic(String task, String description) {
        super(task, description);
    }

    public Epic(String task, String description, int id, Status status, ArrayList<Integer> subtaskCodes) {
        super(task, description, id, status);
    }

    public ArrayList<Integer> getSubtaskCodes() {
        return subtaskCodes;
    }

    public void addSubtaskCode(int code){
        subtaskCodes.add(code);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status + '\'' +
                ", subtaskCodes=" + subtaskCodes +
                '}';
    }
}
