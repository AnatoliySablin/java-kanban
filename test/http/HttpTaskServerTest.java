package http;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class HttpTaskServerTest {
    @Test
    public void checkStartAndStopServer() {
        TaskManager manager = new InMemoryTaskManager();
        HttpTaskServer taskServer = new HttpTaskServer(manager);
        try {
            taskServer.startServer();
        } catch (IOException e) {
            fail("Test failed: " + e.getMessage());
        } finally {
            taskServer.stopServer();
        }
    }
}
