public class Subtask extends Task {
    private int epicId;

    public Subtask(String task, String description) {
        super(task, description);
    }

    public Subtask( String task, String description, int id, Status status, int epicId){
        super(task, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


}
