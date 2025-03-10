package manager.server;

import com.sun.net.httpserver.HttpExchange;
import manager.HistoryManager;

import java.io.IOException;

// Класс-обработчик HTTP-запросов для работы с историей просмотров
public class HistoryHandler extends BaseHttpHandler {
    private final HistoryManager historyManager; // Менеджер истории для взаимодействия с данными

    // Конструктор принимает менеджер истории
    public HistoryHandler(HistoryManager historyManager) {
        this.historyManager = historyManager; // Сохраняем менеджер истории
    }

    // Метод обработки HTTP-запросов
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod(); // Получаем метод запроса
        if ("GET".equals(method)) { // Если запрос GET
            try {
                // Получаем список истории и преобразуем в JSON
                String response = gson.toJson(historyManager.getHistory()); ///////
                sendText(exchange, response, 200);
            } catch (Exception e) {
                sendInternalError(exchange, String.format("Внутренняя ошибка сервера: %s", e.getMessage()));
            }
        } else {
            // Если метод не GET, отправляем ошибку 405 (метод не поддерживается)
            sendText(exchange, "{\"error\": \"Метод не поддерживается\"}", 405);
        }
    }
}
