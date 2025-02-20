package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    public void SubtasksWithEqualIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Сходить в магазин", "Купить хлеб", 10, Status.NEW, 5);
        Subtask subtask2 = new Subtask("Сходить в другой магазин", "Купить молоко", 10, Status.DONE, 5);
        assertEquals(subtask1, subtask2,
                "Ошибка! Наследники класса Task должны быть равны друг другу, если равен их id;");
    }
}