import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status = Status.NEW;

   public Task(String task, String description) {
       this.name = task;
       this.description = description;
   }

   public Task(String task, String description, int id, Status status){
       this.name = task;
       this.description = description;
       this.id = id;
       this.status = status;
   }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}