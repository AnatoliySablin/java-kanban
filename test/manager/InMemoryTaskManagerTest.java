package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exceptions.ManagerValidationException;

import java.time.Duration;
import java.time.ZonedDateTime;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        super.manager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    public void intervalsOverlap() {
        ZonedDateTime start1 = ZonedDateTime.now();
        ZonedDateTime end1 = start1.plusHours(1);
        ZonedDateTime start2 = ZonedDateTime.now();
        ZonedDateTime end2 = start2.plusHours(1);

        Task task1 = new Task("Task1", "description", Status.NEW);
        task1.setStartTime(start1.toLocalDateTime());
        task1.setDuration(Duration.between(start1.toLocalDateTime(), end1.toLocalDateTime()));
        manager.addTask(task1);

        Task task2 = new Task("Task2", "description", Status.NEW);
        task2.setStartTime(start2.toLocalDateTime());
        task2.setDuration(Duration.between(start2, end2));
        ManagerValidationException thrown = assertThrows(ManagerValidationException.class,
                () -> manager.addTask(task2));

        assertEquals("Ошибка. Задачи пересекаются по времени выполнения: " + task2, thrown.getMessage());
    }

    @Test
    public void intervalsNotOverlap() {
        ZonedDateTime start1 = ZonedDateTime.now();
        ZonedDateTime end1 = start1.plusHours(1);
        ZonedDateTime start2 = ZonedDateTime.now().plusDays(1);
        ZonedDateTime end2 = start2.plusHours(1);

        Task task1 = new Task("Task1", "description", Status.NEW);
        task1.setStartTime(start1.toLocalDateTime());
        task1.setDuration(Duration.between(start1, end1));
        manager.addTask(task1);

        Task task2 = new Task("Task2", "description", Status.NEW);
        task2.setStartTime(start2.toLocalDateTime());
        task2.setDuration(Duration.between(start2, end2));
        assertDoesNotThrow(() -> manager.addTask(task2));
    }
}
