package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
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

import static model.Status.NEW;
import static org.junit.jupiter.api.Assertions.*;

public class TasksHandlerTest {
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
    public void makeTaskShouldAddOneTaskWith201() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks");
        Task task = new Task("Task", "description", NEW);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void addTaskShouldReturn406() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks");
        HttpRequest request;
        HttpResponse<String> response;

        LocalDateTime start1 = LocalDateTime.now();
        LocalDateTime end1 = start1.plusHours(1);
        LocalDateTime start2 = LocalDateTime.now();
        LocalDateTime end2 = start2.plusHours(1);

        Task task1 = new Task("Task1", "description", NEW);
        task1.setStartTime(start1);
        task1.setDuration(Duration.between(start1, end1));
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        Task task2 = new Task("Task2", "description", NEW);
        task2.setStartTime(start2);
        task2.setDuration(Duration.between(start2, end2));
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void getTaskShouldGetAllTaskWith200() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks");
        HttpRequest request;
        HttpResponse<String> response;

        Task task1 = new Task("Task1", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task task2 = new Task("Task2", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        URI urlGetAll = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder()
                .uri(urlGetAll)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> expectList = manager.getTasks();
        List<Task> actualList = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        for (int i = 0; i < actualList.size(); i++) {
            Task actualTask = actualList.get(i);
            Task expectTask = expectList.get(i);
            assertEquals(expectTask.getId(), actualTask.getId());
            assertEquals(expectTask.getName(), actualTask.getName());
            assertEquals(expectTask.getStatus(), actualTask.getStatus());
        }
    }

    @Test
    public void getTaskShouldGetOneTaskWith200() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks");
        HttpRequest request;
        HttpResponse<String> response;
        int taskId = 1;

        Task task1 = new Task("Task1", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Task task2 = new Task("Task2", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task2)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());


        URI urlGet = URI.create("http://localhost:8080/tasks/" + taskId);
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task expect = manager.getTask(taskId);
        Task actual = gson.fromJson(response.body(), Task.class);

        assertEquals(expect.getId(), actual.getId());
        assertEquals(expect.getName(), actual.getName());
        assertEquals(expect.getStatus(), actual.getStatus());
    }

    @Test
    public void getTaskShouldReturn404() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks");
        HttpRequest request;
        HttpResponse<String> response;

        Task task1 = new Task("Task1", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        URI urlGet = URI.create("http://localhost:8080/tasks/2");
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteTaskShouldReturn200() throws IOException, InterruptedException {
        URI urlPost = URI.create("http://localhost:8080/tasks");
        HttpRequest request;
        HttpResponse<String> response;

        Task task1 = new Task("Task1", "description", NEW);
        request = HttpRequest.newBuilder()
                .uri(urlPost)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        URI urlGet = URI.create("http://localhost:8080/tasks/1");
        request = HttpRequest.newBuilder()
                .uri(urlGet)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getTasks().size());
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
