package http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler<Subtask> {
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
}
