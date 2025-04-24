package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.adapters.DurationAdapter;
import model.adapters.LocalDateTimeAdapter;
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

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SubtasksHandlerTest {
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
        manager.clearSubtaskList();
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
    public void addSubtaskShouldAddWith201() throws IOException, InterruptedException {
        URI urlPostEpic = URI.create("http://localhost:8080/epics");
        URI urlPostSubtask = URI.create("http://localhost:8080/subtasks");
        HttpRequest request;
        HttpResponse<String> response;

        Epic epic = new Epic("Epic1", "description", 1, NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPostEpic)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask1 = new Subtask("Subtask1", "description", NEW, 1);
        request = HttpRequest.newBuilder()
                .uri(urlPostSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask2 = new Subtask("Subtask2", "description", NEW, 1);
        request = HttpRequest.newBuilder()
                .uri(urlPostSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void getSubtaskShouldReturn400() throws IOException, InterruptedException {
        URI urlPostEpic = URI.create("http://localhost:8080/epics");
        URI urlPostSubtask = URI.create("http://localhost:8080/subtasks");
        HttpRequest request;
        HttpResponse<String> response;

        Epic epic = new Epic("Epic1", "description", 1, NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPostEpic)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask1 = new Subtask("Subtask1", "description", NEW, 1);
        request = HttpRequest.newBuilder()
                .uri(urlPostSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask2 = new Subtask("Subtask2", "description", NEW, 1);
        request = HttpRequest.newBuilder()
                .uri(urlPostSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        URI urlGet = URI.create("http://localhost:8080/epics/qwerty");
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    public TaskManager getManager() {
        return manager;
    }

    public HttpTaskServer getTaskServer() {
        return taskServer;
    }

    public HttpClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }
}
