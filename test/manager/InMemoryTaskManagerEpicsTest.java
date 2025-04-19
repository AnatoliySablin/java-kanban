package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InMemoryTaskManagerEpicsTest {
    private TaskManager taskManager;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void preparing() {
        taskManager = new InMemoryTaskManager();

        epic1 = new Epic("Эпик1", "Описание1", 1, Status.NEW);
        epic2 = new Epic("Эпик2", "Описание2", 2, Status.NEW);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask1", "Description 1", Status.NEW, LocalDateTime.of(2025, 4, 17,
                20, 40),
                Duration.ofMinutes(15), epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "Description 2", Status.NEW, LocalDateTime.of(2025, 4, 17,
                21, 40),
                Duration.ofMinutes(15), epic1.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

    }

    @Test
    public void twoEpicsWithSameIdShouldBeEquals() {
        assertEquals(epic1, taskManager.getEpic(epic1.getId()));
    }

    @Test
    public void twoSubtasksWithSameIdShouldBeEquals() {
        assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()));
    }

    @Test
    public void shouldReturnArrayOfEpics() {
        List<Epic> epics = taskManager.getEpics();
        List<Epic> epics1 = new ArrayList<>(Arrays.asList(epic1, epic2));

        assertEquals(epics, epics1);
    }

    @Test
    public void shouldReturnArrayOfSubtasks() {
        List<Subtask> subtasks = taskManager.getSubtasks();
        List<Subtask> subtasks1 = new ArrayList<>(Arrays.asList
                (subtask1,
                        subtask2));

        assertEquals(subtasks, subtasks1);
    }

    @Test
    public void shouldReturnEpicByItsId() {
        assertEquals(taskManager.getEpic(epic1.getId()), epic1);
    }

    @Test
    public void shouldReturnSubtaskByItsId() {
        assertEquals(subtask1, taskManager.getSubtask(subtask1.getId()));
    }

    @Test
    public void shouldAddEpic() {
        Epic epic = new Epic("AAA", "bbb");
        taskManager.addEpic(epic);

        assertEquals(taskManager.getEpic(epic.getId()), epic);
    }

    @Test
    public void shouldAddSubtask() {
        Subtask subtask = new Subtask("Subtask1", "description", NEW, LocalDateTime.now(), Duration.ofMinutes(10),
                epic1.getId());
        taskManager.addSubtask(subtask);

        assertEquals(taskManager.getSubtask(subtask.getId()), subtask);
    }

    @Test
    public void shouldUpdateEpicStatusWithChangingSubtasksStatus() {
        Subtask subtask = taskManager.getSubtask(subtask1.getId());
        subtask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        //Переделать
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic1.getId()).getStatus());
    }

    @Test
    public void shouldReturnNullAfterDeleteEpic() {
        taskManager.deleteEpic(epic1.getId());

        assertNull(taskManager.getEpic(epic1.getId()));
    }

    @Test
    public void shouldReturnNullAfterDeleteSubtask() {
        Subtask subtask = taskManager.getSubtasks().get(0);
        taskManager.deleteSubtask(subtask.getId());

        assertNull(taskManager.getSubtask(subtask.getId()));
    }
}