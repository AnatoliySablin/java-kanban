package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = new Task("Сходить в магазин", "Купить хлеб", 10, Status.NEW, LocalDateTime.of(2025, 4, 8, 20, 25 ), Duration.ofMinutes(10));
        Task task2 = new Task("Сходить в другой магазин", "Купить молоко", 10, Status.DONE, LocalDateTime.of(2025, 4, 9, 20, 25 ), Duration.ofMinutes(10));
        assertEquals(task1, task2,
                "Ошибка! Экземпляры класса Task должны быть равны друг другу, если равен их id;");
    }
}