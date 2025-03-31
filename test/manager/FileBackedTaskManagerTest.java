package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    @TempDir
    Path tempDir;
    private FileBackedTaskManager taskManager;
    private FileBackedTaskManager taskManager2;

    @BeforeEach
    public void setUp() throws IOException {
        File tempFile = Files.createTempFile(tempDir, "tasks", ".csv").toFile();
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    public void testSaveAndLoadEmptyFile() throws IOException {
        taskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(taskManager.file);

        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
    }

    @Test
    public void testSaveAndLoadSeveralTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Epic 1", "Description 2");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Epic 2", "Description 3");
        taskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask1", "Description 4", 1);
        taskManager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask2", "Description 5", 1);
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask3", "Description 6", 2);
        taskManager.addSubtask(subtask3);
        Subtask subtask4 = new Subtask("Subtask4", "Description 7", 2);
        taskManager.addSubtask(subtask4);
        taskManager.save();
        taskManager2 = FileBackedTaskManager.loadFromFile(taskManager.file);
        List<Task> tasks = taskManager2.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task1, tasks.get(0));
        List<Epic> epics = taskManager2.getEpics();
        assertEquals(2, epics.size());
        assertEquals(epic1, epics.get(0));
        assertEquals(epic2, epics.get(1));
        List<Subtask> subtasks = taskManager2.getSubtasks();
        assertEquals(4, subtasks.size());
        assertEquals(subtask1, subtasks.get(0));
        assertEquals(subtask2, subtasks.get(1));
        assertEquals(subtask3, subtasks.get(2));
        assertEquals(subtask4, subtasks.get(3));
    }


}
