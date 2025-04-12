package manager;

import model.Epic;
import model.Status;
import model.Task;
import model.Subtask;
import org.junit.jupiter.api.Test;

import static model.Status.IN_PROGRESS;
import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    T manager;

    @Test
    public void addTaskShouldAddId() {
        Task task = new Task("Task", "description");
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
        Subtask subtask = new Subtask("Subtask1", "description", NEW, epic.getId());
        manager.addSubtask(subtask);
        final Subtask savedTask = manager.getSubtask(subtask.getId());

        assertNotNull(subtask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void updateTaskShouldChangeStatus() {
        Task task = new Task("Task", "description");
        manager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);
        final Task savedTask = manager.getTask(task.getId());

        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void updateEpicShouldNotChangeStatus() {
        Epic epic = new Epic("Epic", "description");
        manager.addEpic(epic);
        epic.setStatus(Status.DONE);
        manager.updateEpic(epic);
        final Epic savedTask = manager.getEpic(epic.getId());

        assertEquals(epic, savedTask, "Задачи не совпадают.");
        assertEquals(IN_PROGRESS, savedTask.getStatus());
    }

    @Test
    public void updateEpicAndSubtaskShouldChangeStatus() {
        Epic epic = new Epic("Epic", "description");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "description", NEW, epic.getId());
        manager.addSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        final Subtask savedTask = manager.getSubtask(subtask.getId());

        assertEquals(subtask, savedTask, "Задачи не совпадают.");
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
    }
}
