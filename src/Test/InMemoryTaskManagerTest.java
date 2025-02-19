package Test;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Status;
import model.Task;
import model.Subtask;
import model.Epic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        final Task task = new Task("Test addNewTask", "Test addNewTask description");
        final Task savedTask = taskManager.getTask(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicAndSubtasks() {
        final Epic flatRenovation = new Epic("Сделать ремонт",
                "Нужно успеть за отпуск");
        final Subtask flatRenovationSubtask1 = new Subtask("Поклеить обои",
                "Обязательно светлые!", flatRenovation.getId());
        final Subtask flatRenovationSubtask2 = new Subtask("Установить новую технику",
                "Старую продать на Авито", flatRenovation.getId());
        final Subtask flatRenovationSubtask3 = new Subtask("Заказать книжный шкаф", "Из темного дерева",
                flatRenovation.getId());
        final Epic savedEpic = taskManager.getEpic(flatRenovation.getId());
        final Subtask savedSubtask1 = taskManager.getSubtask(flatRenovationSubtask1.getId());
        final Subtask savedSubtask2 = taskManager.getSubtask(flatRenovationSubtask2.getId());
        final Subtask savedSubtask3 = taskManager.getSubtask(flatRenovationSubtask3.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertNotNull(savedSubtask2, "Подзадача не найдена.");
        assertEquals(flatRenovation, savedEpic, "Эпики не совпадают.");
        assertEquals(flatRenovationSubtask1, savedSubtask1, "Подзадачи не совпадают.");
        assertEquals(flatRenovationSubtask3, savedSubtask3, "Подзадачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(flatRenovation, epics.getFirst(), "Эпики не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(3, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(savedSubtask1, subtasks.getFirst(), "Подзадачи не совпадают.");
    }


}
