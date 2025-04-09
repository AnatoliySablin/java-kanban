package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    private ArrayList<Task> history;

    @BeforeEach
    void beforeEach() {

        task = new Task("Task", "Описание");
        task.setId(0);
        epic = new Epic("Epic", "Описание");
        epic.setId(1);
        subtask = new Subtask("Subtask", "Описание", Status.NEW, 1);
        subtask.setId(2);
        history = new ArrayList<>();
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void addTest() {

        historyManager.add(task);
        history.addAll(historyManager.getHistory());

        ArrayList<Task> list = new ArrayList<>();
        list.add(task);

        assertNotNull(list, "List is Null");
        assertEquals(list.size(), history.size(), "Size of list do not match");

    }

    @Test
    void duplicatedTasksTest() {

        historyManager.add(task);
        historyManager.add(task);
        history.addAll(historyManager.getHistory());

        ArrayList<Task> list = new ArrayList<>();
        list.add(task);

        assertNotNull(list, "List is Null");
        assertEquals(list.size(), history.size(), "Size of list do not match");
    }

    @Test
    void removeHeadTest() {

        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);
        historyManager.remove(0);

        history.addAll(historyManager.getHistory());

        ArrayList<Task> list = new ArrayList<>();
        list.add(subtask);
        list.add(epic);


        assertNotNull(list, "List is Null");
        assertEquals(list.size(), history.size());

    }

    @Test
    void removeTail() {

        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);
        historyManager.remove(2);

        history.addAll(historyManager.getHistory());

        ArrayList<Task> list = new ArrayList<>();
        list.add(subtask);
        list.add(epic);


        assertNotNull(list, "List is Null");
        assertEquals(list.size(), history.size());
    }
}
