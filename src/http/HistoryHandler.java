package http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager managers) {
        super(managers);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            sendText(exchange, 200, gson.toJson(managers.getHistory()));
        } else {
            sendText(exchange, 405, "Метод запроса не поддерживается.");
        }
    }
}
