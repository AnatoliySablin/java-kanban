package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager managers) {
        super(managers);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_ALL: {
                getAllEpic(exchange);
                break;
            }
            case GET: {
                getEpic(exchange);
                break;
            }
            case GET_WHOLESUBTASK: {
                getWholeSubtasks(exchange);
                break;
            }
            case POST: {
                postEpic(exchange);
                break;
            }
            case DELETE: {
                deleteEpic(exchange);
                break;
            }
            default:
                sendBadRequest(exchange);
        }
    }

    private void getWholeSubtasks(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = managers.getEpic(id);
            sendText(exchange, 200, gson.toJson(managers.getEpicSubtasks(epic.getId())));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    protected void getAllEpic(HttpExchange exchange) throws IOException {
        List<Epic> epicList = managers.getEpics();
        sendText(exchange, 200, gson.toJson(epicList));
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
                sendBadRequest(exchange);
            } else {
                sendText(exchange, 201, gson.toJson(epic));
            }
        } catch (JsonSyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void deleteEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int epicId = Integer.parseInt(pathParts[2]);
            managers.deleteEpic(epicId);
            sendText(exchange, 200, "");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }
}
