package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler<Epic> implements HttpHandler {
    public EpicsHandler(TaskManager managers) {
        super(managers, Epic.class);
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
            case GET_WHOLESUBTASK: {
                getWholeSubtasks(exchange);
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

    private void getWholeSubtasks(HttpExchange exchange) throws IOException {
        Gson gson = getGson();
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            int id = Integer.parseInt(pathParts[2]);
            Epic epic = getManagers().getEpic(id);
            sendText(exchange, 200, gson.toJson(getManagers().getEpicSubtasks(epic.getId())));
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }
}
