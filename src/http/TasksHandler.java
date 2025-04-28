package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager managers) {
        super(managers);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_ALL: {
                getAllTask(exchange);
                break;
            }
            case GET: {
                getTask(exchange);
                break;
            }
            case POST: {
                postTask(exchange);
                break;
            }
            case DELETE: {
                deleteTask(exchange);
                break;
            }
            default:
                sendBadRequest(exchange);
        }
    }

    protected void getAllTask(HttpExchange exchange) throws IOException {
        List<Task> taskList = managers.getTasks();
        sendText(exchange, 200, gson.toJson(taskList));
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

    protected void postTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            sendText(exchange, 400, "Request body is empty");
            return;
        }
        try {
            Task task = gson.fromJson(body, Task.class);
            if (!managers.getTasks().contains(task)) {
                int result = managers.addTask(task);
                if (result == -1) {
                    sendHasInteractions(exchange);
                } else {
                    sendText(exchange, 201, gson.toJson(task));
                }
            } else {
                managers.updateTask(task);
                if (managers.getPrioritizedTasks().contains(task)) {
                    sendText(exchange, 201, gson.toJson(task));
                } else {
                    sendHasInteractions(exchange);
                }
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
            sendText(exchange, 200, "");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }
}
