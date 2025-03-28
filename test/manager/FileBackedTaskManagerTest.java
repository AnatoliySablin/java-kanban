package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    public static Path path = Paths.get("./test/fileDataTest");
    File file = new File(String.valueOf(path));
    FileBackedTaskManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTaskManager(file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.writeString(path, "");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void loadFromFileTest() {
        Task task = new Task("name", "descr", Status.NEW);
        Task task2 = new Task("name", "descr", Status.NEW);
        manager.addTask(task);
        manager.addTask(task2);

        FileBackedTaskManager managerTest = FileBackedTaskManager.loadFromFile(file);
        assertEquals(manager.getTasks(), managerTest.getTasks());
    }
}
