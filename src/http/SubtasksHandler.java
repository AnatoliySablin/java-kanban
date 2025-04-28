package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager managers) {
        super(managers);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_ALL: {
                getAllSubtask(exchange);
                break;
            }
            case GET: {
                getSubtask(exchange);
                break;
            }
            case POST: {
                postSubtask(exchange);
                break;
            }
            case DELETE: {
                deleteSubtask(exchange);
                break;
            }
            default:
                sendBadRequest(exchange);
        }
    }

    protected void getAllSubtask(HttpExchange exchange) throws IOException {
        List<Subtask> subtaskList = managers.getSubtasks();
        sendText(exchange, 200, gson.toJson(subtaskList));
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

    protected void postSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            sendText(exchange, 400, "Request body is empty");
            return;
        }
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (!managers.getSubtasks().contains(subtask)) {
                int result = managers.addSubtask(subtask);
                if (result == -1) {
                    sendHasInteractions(exchange);
                } else {
                    sendText(exchange, 201, gson.toJson(subtask));
                }
            } else {
                managers.updateSubtask(subtask);
                if (managers.getPrioritizedTasks().contains(subtask)) {
                    sendText(exchange, 201, gson.toJson(subtask));
                } else {
                    sendHasInteractions(exchange);
                }
            }
        } catch (JsonSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deleteSubtask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[2]);
            managers.deleteSubtask(id);
            sendText(exchange, 200, "");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }
}
