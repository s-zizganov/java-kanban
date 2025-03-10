package manager.server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

// Класс-обработчик HTTP-запросов для получения приоритетного списка задач (отсортированных по времени начала)
public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager; // Менеджер задач для взаимодействия с данными

    // Конструктор принимает менеджер задач
    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager; // Сохраняем менеджер задач
    }

    // Метод обработки HTTP-запросов
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod(); // Получаем метод запроса

        if ("GET".equals(method)) {
            try {
                // Получаем отсортированный список задач и преобразуем в JSON
                String response = gson.toJson(taskManager.getPrioritizedTasks());
                sendText(exchange, response, 200); // Отправляем ответ с кодом 200
            } catch (IOException e) {
                // При ошибке отправляем код 500
                sendInternalError(exchange, String.format("Внутренняя ошибка сервера: %s", e.getMessage()));
            }
        } else {
            // Если метод не GET, отправляем ошибку 405 (метод не поддерживается)
            sendText(exchange, "{\"error\": \"Метод не поддерживается\"}", 405);
        }
    }
}
