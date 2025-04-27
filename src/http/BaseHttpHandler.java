package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import exceptions.ManagerValidationException;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Task;
import model.adapters.DurationAdapter;
import model.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public abstract class BaseHttpHandler<T extends Task> {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();
    private final TaskManager managers;
    private Class<T> type;

    public BaseHttpHandler(TaskManager managers) {
        this.managers = managers;
    }

    public BaseHttpHandler(TaskManager managers, Class<T> type) {
        this.managers = managers;
        this.type = type;
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
        List<T> taskList = getManagers().getTasks().stream()
                .filter(task -> type.isInstance(task))
                .map(type::cast)
                .toList();
        sendText(exchange, 200, gson.toJson(taskList));
    }

    protected void getTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[2]);
            T task = type.cast(getManagers().getTask(id));
            sendText(exchange, 200, gson.toJson(task));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    protected void postTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, type);
        try {
            getManagers().addTask(task);
            sendText(exchange, 201, gson.toJson(task));
        } catch (ManagerValidationException ex) {
            sendHasInteractions(exchange);
        }
    }

    protected void deleteTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[2]);
            getManagers().deleteTask(id);
            sendText(exchange, 200, "");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    protected void sendText(HttpExchange httpExchange, int code, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(code, resp.length);
        httpExchange.getResponseBody().write(resp);
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


    public TaskManager getManagers() {
        return managers;
    }

    public Gson getGson() {
        return gson;
    }
}
