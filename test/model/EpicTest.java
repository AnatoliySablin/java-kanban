package model;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    public void EpicsWithEqualIdShouldBeEqual() {
        Epic epic1 = new Epic("Купить квартиру", "Сделать ремонт", 7, Status.NEW);
        Epic epic2 = new Epic("Устроиться на работу", "Подготовиться к собеседованию", 7,
                Status.IN_PROGRESS);
        assertEquals(epic1, epic2,
                "Ошибка! Наследники класса Task должны быть равны друг другу, если равен их id;");
    }

    @Test
    void lifespanOfEpicTest() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "descr", Status.DONE,
                LocalDateTime.of(2025, 4, 8, 20, 20), Duration.ofMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask("Subtask1", "descr", Status.NEW,
                LocalDateTime.of(2025, 4, 8, 20, 30), Duration.ofMinutes(10), epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.lifespanOfEpic(epic);

        assertEquals(subtask2.getEndTime(), epic.getEndTime(), "Неверное время завершения эпика");
        assertEquals(subtask1.getStartTime(), epic.getStartTime(), "Неверное время начала эпика");
        assertEquals(Duration.ofMinutes(20), epic.getDuration(), "Неверная продолжительность эпика");
    }

    @Test
    void epicStatusSubTasksNew() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "descr", Status.NEW,
                LocalDateTime.of(2025, 4, 8, 20, 22), Duration.ofMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask("Subtask1", "descr", Status.NEW,
                LocalDateTime.of(2025, 4, 9, 23, 22), Duration.ofMinutes(10), epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(epic.getStatus(), Status.NEW, "Неверный статус эпика");
    }

    @Test
    void epicStatusSubTasksDone() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "descr", Status.DONE,
                LocalDateTime.of(2025, 4, 8, 20, 22), Duration.ofMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask("Subtask1", "descr", Status.DONE,
                LocalDateTime.of(2025, 4, 9, 23, 22), Duration.ofMinutes(10), epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(epic.getStatus(), Status.DONE, "Неверный статус эпика");
    }

    @Test
    void epicUpdateSubTask() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "descr", Status.DONE,
                LocalDateTime.of(2025, 4, 8, 20, 22), Duration.ofMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask("Subtask1", "descr", Status.NEW,
                LocalDateTime.of(2025, 4, 9, 23, 22), Duration.ofMinutes(10), epic.getId());

        taskManager.addSubtask(subtask1);
        assertEquals(epic.getStatus(), Status.DONE, "Неверный статус эпика");

        taskManager.addSubtask(subtask2);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Неверный статус эпика");
    }

    @Test
    void epicStatusSubTasksInProgress() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "descr", Status.IN_PROGRESS,
                LocalDateTime.of(2025, 4, 8, 20, 22), Duration.ofMinutes(10), epic.getId());
        Subtask subtask2 = new Subtask("Subtask1", "descr", Status.IN_PROGRESS,
                LocalDateTime.of(2025, 4, 9, 23, 22), Duration.ofMinutes(10), epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Неверный статус эпика");
    }
}