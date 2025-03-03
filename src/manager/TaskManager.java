package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    int addTask(Task task);

    int addEpic(Epic epic);

    int addSubtask(Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void clearTasksList();

    boolean clearEpicList();

    boolean clearSubtaskList();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    boolean deleteTask(int id);

    boolean deleteEpic(int id);

    boolean deleteSubtask(int id);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    ;

    List<Task> getHistory();

    List<Subtask> getEpicSubtasks(int epicId);
}
