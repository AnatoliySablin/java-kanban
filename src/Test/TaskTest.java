package Test;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = new Task("Сходить в магазин", "Купить хлеб", 10, Status.NEW);
        Task task2 = new Task("Сходить в другой магазин", "Купить молоко", 10, Status.DONE);
        assertEquals(task1, task2,
                "Ошибка! Экземпляры класса Task должны быть равны друг другу, если равен их id;");
    }
}