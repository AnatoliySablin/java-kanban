package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Status;
import model.Task;
import model.adapters.DurationAdapter;
import model.adapters.LocalDateTimeAdapter;
import model.typeTokens.TaskListTypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class HistoryHandlerTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeEach
    public void setUp() {
        manager.clearTasksList();
        try {
            taskServer.startServer();
        } catch (IOException e) {
            fail("Test failed: " + e.getMessage());
        }
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void getHistoryShouldReturn200() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks");
        HttpRequest request;
        HttpResponse<String> response;

        Task task1 = new Task("Task 1", "Description 1", 1, Status.NEW, LocalDateTime.of(2025, 4, 17, 19, 40),
                Duration.ofMinutes(10));
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task task2 = new Task("Task 2", "Description 1", 1, Status.NEW, LocalDateTime.of(2025, 4, 17, 19, 30),
                Duration.ofMinutes(10));
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task task3 = new Task("Task 3", "Description 1", 1, Status.NEW, LocalDateTime.of(2025, 4, 17, 19, 50),
                Duration.ofMinutes(10));
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        URI urlGet = URI.create("http://localhost:8080/tasks/" + 1);
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        urlGet = URI.create("http://localhost:8080/tasks/" + 3);
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        urlGet = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> expectList = manager.getHistory();
        List<Task> actualList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        for (int i = 0; i < actualList.size(); i++) {
            Task actualTask = actualList.get(i);
            Task expectTask = expectList.get(i);
            assertEquals(expectTask.getId(), actualTask.getId());
            assertEquals(expectTask.getName(), actualTask.getName());
            assertEquals(expectTask.getStatus(), actualTask.getStatus());
        }
    }

}
