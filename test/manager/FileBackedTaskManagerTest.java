package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        try {
            tempFile = File.createTempFile("test", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }
        super.manager = FileBackedTaskManager.loadFromFile(tempFile);
    }

    @Test
    public void loadEmptyFileShouldReturnEmptyManager() {
        assertEquals(manager.getTasks().size(), 0);
    }

    @Test
    public void saveEmptyManagerToFileShouldBeEmptyFile() {
        manager.save();
        assertEquals(manager.getTasks().size(), FileBackedTaskManager.loadFromFile(tempFile).getTasks().size());
    }

    @Test
    public void testSaveAndLoadSeveralTasks() {
        taskManager = new FileBackedTaskManager(tempFile);
        Task task1 = new Task("Task 1", "Description 1", 1, Status.NEW, LocalDateTime.of(2025, 4, 17, 19, 40),
                Duration.ofMinutes(10));
        taskManager.addTask(task1);
        Epic epic1 = new Epic("Epic 1", "Description 2", 1, Status.NEW);
        taskManager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "Description 4", 3, Status.NEW, 2, LocalDateTime.of(2025, 4, 17,
                20, 40),
                Duration.ofMinutes(15));
        taskManager.addSubtask(subtask1);
        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = taskManager2.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task1, tasks.get(0));
        assertEquals(task1.getName(), tasks.get(0).getName());
        assertEquals(task1.getDescription(), tasks.get(0).getDescription());
        assertEquals(task1.getStatus(), tasks.get(0).getStatus());
        assertEquals(task1.getStartTime(), tasks.get(0).getStartTime());
        assertEquals(task1.getDuration(), tasks.get(0).getDuration());
        List<Epic> epics = taskManager2.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic1, epics.get(0));
        assertEquals(epic1.getName(), epics.get(0).getName());
        assertEquals(epic1.getDescription(), epics.get(0).getDescription());
        assertEquals(epic1.getStatus(), epics.get(0).getStatus());
        assertEquals(epic1.getSubtaskId(), epics.get(0).getSubtaskId());
        assertEquals(epic1.getStartTime(), epics.get(0).getStartTime());
        assertEquals(epic1.getEndTime(), epics.get(0).getEndTime());
        List<Subtask> subtasks = taskManager2.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask1, subtasks.get(0));
        assertEquals(subtask1.getName(), subtasks.get(0).getName());
        assertEquals(subtask1.getDescription(), subtasks.get(0).getDescription());
        assertEquals(subtask1.getStatus(), subtasks.get(0).getStatus());
        assertEquals(subtask1.getEpicId(), subtasks.get(0).getEpicId());
        assertEquals(subtask1.getStartTime(), subtasks.get(0).getStartTime());
        assertEquals(subtask1.getDuration(), subtasks.get(0).getDuration());
        assertEquals(taskManager.getPrioritizedTasks(), taskManager2.getPrioritizedTasks());
    }


    @Test
    public void allSubtasksWithStatusNewAndDone() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(manager.getTasks().size(), loadedManager.getTasks().size());

        List<Task> originalTasks = new ArrayList<>(manager.getTasks());
        List<Task> loadedTasks = new ArrayList<>(loadedManager.getTasks());
        for (int i = 0; i < originalTasks.size(); i++) {
            Task originalTask = originalTasks.get(i);
            Task loadedTask = loadedTasks.get(i);
            assertEquals(originalTask.getId(), loadedTask.getId());
            assertEquals(originalTask.getName(), loadedTask.getName());
            assertEquals(originalTask.getStatus(), loadedTask.getStatus());
        }
    }
}
