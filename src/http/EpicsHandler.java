package http;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;

public class EpicsHandler extends BaseHttpHandler<Epic> {
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
}
