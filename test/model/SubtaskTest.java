package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    public void SubtasksWithEqualIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Сходить в магазин", "Купить хлеб", Status.NEW, LocalDateTime.of(2025, 4, 8, 20, 22 ), Duration.ofMinutes(10), 5);
        Subtask subtask2 = new Subtask("Сходить в другой магазин", "Купить молоко", Status.DONE, LocalDateTime.of(2025, 4, 8, 20, 22 ), Duration.ofMinutes(10), 5);
        assertEquals(subtask1, subtask2,
                "Ошибка! Наследники класса Task должны быть равны друг другу, если равен их id;");
    }
}