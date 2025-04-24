package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHandler(TaskManager managers) {
        super(managers, Subtask.class);
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
}
