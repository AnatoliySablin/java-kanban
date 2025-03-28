package model;

import org.junit.jupiter.api.Test;

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
}