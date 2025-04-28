package http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler<Task> {
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
}
