package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.adapters.DurationAdapter;
import model.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class BaseHttpHandler<T extends Task> implements HttpHandler {
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();
    protected final TaskManager managers;

    public BaseHttpHandler(TaskManager managers) {
        this.managers = managers;
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET" -> {
                if (pathParts.length == 2)
                    return Endpoint.GET_ALL;
                else if (pathParts.length == 3)
                    return Endpoint.GET;
                else if (pathParts.length == 4) {
                    if (pathParts[3].equals("subtasks"))
                        return Endpoint.GET_WHOLESUBTASK;
                }
            }
            case "POST" -> {
                if (pathParts.length == 2)
                    return Endpoint.POST;
            }
            case "DELETE" -> {
                if (pathParts.length == 3)
                    return Endpoint.DELETE;
            }
            default -> {
                return Endpoint.UNKNOWN;
            }
        }

        return Endpoint.UNKNOWN;
    }

    protected void getAllTask(HttpExchange exchange) throws IOException {
        List<Task> taskList = managers.getTasks();
        sendText(exchange, 200, gson.toJson(taskList));
    }

    protected void getAllEpic(HttpExchange exchange) throws IOException {
        List<Epic> epicList = managers.getEpics();
        sendText(exchange, 200, gson.toJson(epicList));
    }

    protected void getAllSubtask(HttpExchange exchange) throws IOException {
        List<Subtask> subtaskList = managers.getSubtasks();
        sendText(exchange, 200, gson.toJson(subtaskList));
    }

    protected void getTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[2]);
            Task task = managers.getTask(id);
            sendText(exchange, 200, gson.toJson(task));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    protected void getEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int epicID = Integer.parseInt(pathParts[2]);
            Epic epic = managers.getEpic(epicID);
            sendText(exchange, 200, gson.toJson(epic));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    protected void getSubtask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[2]);
            Subtask subtask = managers.getSubtask(id);
            sendText(exchange, 200, gson.toJson(subtask));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    protected void postTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            sendText(exchange, 400, "Request body is empty");
            return;
        }
        try {
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == null || task.getId() == 0) {
                int result = managers.addTask(task);
                if (result == -1) {
                    sendHasInteractions(exchange);
                } else {
                    sendText(exchange, 201, gson.toJson(task));
                }
            } else {
                managers.updateTask(task);
                sendText(exchange, 201, gson.toJson(task));
            }
        } catch (JsonSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void postEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            sendText(exchange, 400, "Request body is empty");
            return;
        }
        try {
            Epic epic = gson.fromJson(body, Epic.class);
                int result = managers.addEpic(epic);
                if (result == -1) {
                    sendHasInteractions(exchange);
                } else {
                    sendText(exchange, 201, gson.toJson(epic));
                }
        } catch (JsonSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void postSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            sendText(exchange, 400, "Request body is empty");
            return;
        }
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getId() == null || subtask.getId() == 0) {
                int result = managers.addSubtask(subtask);
                if (result == -1) {
                    sendHasInteractions(exchange);
                } else {
                    sendText(exchange, 201, gson.toJson(subtask));
                }
            } else {
                managers.updateSubtask(subtask);
                sendText(exchange, 201, gson.toJson(subtask));
            }
        } catch (JsonSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deleteTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[2]);
            managers.deleteTask(id);
            sendText(exchange, 404, "");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    protected void deleteEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int epicId = Integer.parseInt(pathParts[2]);
            managers.deleteEpic(epicId);
            sendText(exchange, 404, "");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    protected void deleteSubtask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[2]);
            managers.deleteSubtask(id);
            sendText(exchange, 404, "");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    protected void sendText(HttpExchange httpExchange, int code, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(code, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    }

    protected void sendBadRequest(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(400, 0);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(404, 0);
        httpExchange.close();
    }

    protected void sendHasInteractions(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(406, 0);
        httpExchange.close();
    }
}
