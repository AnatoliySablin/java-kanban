package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    private File temp;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    public void setUp() throws IOException {
        temp = File.createTempFile("temp", "csv");
        taskManager = new FileBackedTaskManager(temp);
    }

    @Test
    public void testSaveAndLoadEmptyFile() throws IOException {
        File temp = File.createTempFile("temp", "csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(temp);

        assertTrue(manager.getTasks().isEmpty());
        assertTrue(manager.getEpics().isEmpty());
        assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void testSaveAndLoadSeveralTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Epic 1", "Description 2");
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Description 4", 2);
        taskManager.addSubtask(subtask1);
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(temp);
        List<Task> tasks = taskManager2.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task1.getName(), tasks.get(0).getName());
        assertEquals(task1.getDescription(), tasks.get(0).getDescription());
        assertEquals(task1.getStatus(), tasks.get(0).getStatus());
        List<Epic> epics = taskManager2.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic1, epics.get(0));
        assertEquals(epic1.getName(), epics.get(0).getName());
        assertEquals(epic1.getDescription(), epics.get(0).getDescription());
        assertEquals(epic1.getStatus(), epics.get(0).getStatus());
        List<Subtask> subtasks = taskManager2.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask1, subtasks.get(0));
        assertEquals(subtask1.getName(), subtasks.get(0).getName());
        assertEquals(subtask1.getDescription(), subtasks.get(0).getDescription());
        assertEquals(subtask1.getStatus(), subtasks.get(0).getStatus());
        assertEquals(subtask1.getEpicId(), subtasks.get(0).getEpicId());
    }


}
