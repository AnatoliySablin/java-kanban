package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
