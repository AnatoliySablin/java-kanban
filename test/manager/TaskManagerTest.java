package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    @Test
    public void addTaskShouldAddId() {
        Task task = new Task("Task", "description", 1, NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addTask(task);
        final Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void addEpicShouldAddId() {
        Epic epic = new Epic("Epic", "description");
        manager.addEpic(epic);
        final Epic savedTask = manager.getEpic(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void addSubtaskShouldAddId() {
        Epic epic = new Epic("Epic", "description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "description", NEW, LocalDateTime.now(), Duration.ofMinutes(10),
                epic.getId());
        manager.addSubtask(subtask);
        final Subtask savedTask = manager.getSubtask(subtask.getId());

        assertNotNull(subtask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void updateTaskShouldChangeStatus() {
        Task task = new Task("Task", "description", 1, NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        final Task savedTask = manager.getTask(task.getId());

        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void updateEpicShouldNotChangeStatus() {
        Epic epic = new Epic("Epic", "description", 1, NEW);
        manager.addEpic(epic);

        final Epic savedTask = manager.updateEpic(epic);

        assertEquals(epic, savedTask, "Задачи не совпадают.");
        assertEquals(NEW, savedTask.getStatus());
    }

    @Test
    public void updateEpicAndSubtaskShouldChangeStatus() {
        Epic epic = new Epic("Epic", "description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "description", NEW, LocalDateTime.now(), Duration.ofMinutes(10),
                epic.getId());
        manager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);

        final Subtask savedTask = manager.getSubtask(subtask.getId());

        assertEquals(subtask, savedTask, "Задачи не совпадают.");
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void getTasks() {
        Task task = new Task("Task", "description", 1, NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addTask(task);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task);

        assertEquals(tasks, manager.getTasks());
    }

    @Test
    void getEpics() {
        Epic task = new Epic("Task", "Description");
        manager.addEpic(task);
        ArrayList<Epic> tasks = new ArrayList<>();
        tasks.add(task);

        assertEquals(tasks, manager.getEpics());
    }

    @Test
    void getSubtasks() {
        Epic epic1 = new Epic("Epic 1", "Description 2", 1, Status.NEW);
        Subtask subtask1 = new Subtask("Subtask1", "Description 4", 3, Status.NEW, 1, LocalDateTime.now(),
                Duration.ofMinutes(15));
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        ArrayList<Subtask> tasks = new ArrayList<>();
        tasks.add(subtask1);

        assertEquals(tasks, manager.getSubtasks());
    }

    @Test
    void clearTasksList() {
        Task task = new Task("Task 1", "Description 1", 1, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addTask(task);
        assertEquals(1, manager.getTasks().size());
        manager.clearTasksList();
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void clearEpicList() {
        Epic task = new Epic("Task", "Description");
        manager.addEpic(task);
        assertEquals(1, manager.getEpics().size());
        assertTrue(manager.clearEpicList());
    }

    @Test
    void clearSubtaskList() {
        Epic epic1 = new Epic("Epic 1", "Description 2", 1, Status.NEW);
        Subtask subtask1 = new Subtask("Subtask1", "Description 4", 3, Status.NEW, 1, LocalDateTime.now(),
                Duration.ofMinutes(15));
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        assertTrue(manager.clearSubtaskList());
    }

    @Test
    void getTask() {
        Task task1 = new Task("Task 1", "Description 1", 1, Status.NEW, LocalDateTime.of(2025, 4, 17, 19, 40),
                Duration.ofMinutes(10));
        manager.addTask(task1);
        Task task = manager.getTask(1);

        assertNotNull(task);
        assertEquals("Task 1", task.getName());
        assertEquals("Description 1", task.getDescription());
        assertEquals(1, task.getId());
        assertEquals(LocalDateTime.of(2025, 4, 17, 19, 40), task.getStartTime());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(Duration.ofMinutes(10), task.getDuration());
    }

    @Test
    void getEpic() {
        Epic task1 = new Epic("Epic 1", "Description 2", 1, Status.NEW);
        manager.addEpic(task1);
        Epic task = manager.getEpic(1);

        assertNotNull(task);
        assertEquals("Epic 1", task.getName());
        assertEquals("Description 2", task.getDescription());
        assertEquals(1, task.getId());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void getSubtask() {
        Epic epic1 = new Epic("Epic 1", "Description 2", 1, Status.NEW);
        Subtask subtask1 = new Subtask("Subtask1", "Description 4", 3, Status.NEW, 1, LocalDateTime.of(2025, 4, 17,
                19, 40),
                Duration.ofMinutes(10));
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        Subtask task = manager.getSubtask(2);

        assertNotNull(task);
        assertEquals("Subtask1", task.getName());
        assertEquals("Description 4", task.getDescription());
        assertEquals(2, task.getId());
        assertEquals(LocalDateTime.of(2025, 4, 17, 19, 40), task.getStartTime());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(Duration.ofMinutes(10), task.getDuration());
        assertEquals(1, task.getEpicId());
    }

    @Test
    void deleteTask() {
        Task task = new Task("Task 1", "Description 1", 1, Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        manager.addTask(task);

        assertTrue(manager.deleteTask(1));
    }

    @Test
    void deleteEpic() {
        Epic task = new Epic("Task", "Description", 1, NEW);
        manager.addEpic(task);

        assertTrue(manager.deleteEpic(1));
    }

    @Test
    void deleteSubtask() {
        Epic epic1 = new Epic("Epic 1", "Description 2", 1, Status.NEW);
        Subtask subtask1 = new Subtask("Subtask1", "Description 4", 3, Status.NEW, 1, LocalDateTime.now(),
                Duration.ofMinutes(15));
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        assertTrue(manager.deleteSubtask(2));
    }

    @Test
    void getEpicSubtasks() {
        Epic epic1 = new Epic("Epic 1", "Description 2", 1, Status.NEW);
        Subtask subtask1 = new Subtask("Subtask1", "Description 4", 3, Status.NEW, 1, LocalDateTime.of(2025, 4, 17,
                19, 40),
                Duration.ofMinutes(10));
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        List<Subtask> subtasks = manager.getEpicSubtasks(epic1.getId());

        assertEquals(1, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
    }

    @Test
    void getPrioritizedTasks() {
        Epic epic1 = new Epic("Epic 1", "Description 2", 1, Status.NEW);
        Subtask subtask1 = new Subtask("Subtask1", "Description 4", 3, Status.NEW, 1, LocalDateTime.of(2025, 4, 17,
                19, 40),
                Duration.ofMinutes(10));
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(1, prioritizedTasks.size());
    }

    @Test
    void getHistoryTasksShouldReturnCorrectOrder() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("Task 1", "Описание", 1, Status.NEW, LocalDateTime.of(2025, 4, 17, 19, 40),
                Duration.ofMinutes(60));
        Task task2 = new Task("Task 2", "Описание", 2, Status.NEW, LocalDateTime.of(2025, 4, 17, 21, 40),
                Duration.ofMinutes(60));
        Task task3 = new Task("Task 3", "Описание", 3, Status.NEW, LocalDateTime.of(2025, 4, 17, 23, 40),
                Duration.ofMinutes(60));

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

}
