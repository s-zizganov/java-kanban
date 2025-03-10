package manager.server;

import com.sun.net.httpserver.HttpExchange;
import entity.Subtask;
import exception.NotFoundException;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Класс-обработчик HTTP-запросов для работы с подзадачами
public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager; // Менеджер задач для взаимодействия с данными

    // Конструктор принимает менеджер задач
    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager; // Сохраняем менеджер задач
    }

    // Метод обработки HTTP-запросов
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod(); // Получаем метод запроса
        String path = exchange.getRequestURI().getPath(); // Получаем путь запроса, например, /subtasks или /subtasks/1)
        String[] pathParts = path.split("/"); // Разбиваем путь на части по слешу

        try {
            switch (method) {
                case "GET": // Обработка GET-запросов (получение данных)
                    if (pathParts.length == 2) { // Если путь просто /subtasks
                        // Получаем список всех подзадач и отправляем в формате JSON
                        String response = gson.toJson(taskManager.getAllSubtask());
                        sendText(exchange, response, 200);
                    } else if (pathParts.length == 3) { // Если путь /subtasks/{id}
                        int id = Integer.parseInt(pathParts[2]); // Извлекаем ID подзадачи из пути
                        Subtask subtask = taskManager.getSubtask(id); // Получаем подзадачу по ID
                        sendText(exchange, gson.toJson(subtask), 200); // Отправляем подзадачу в JSON
                    }
                    break;

                case "POST": // Обработка POST-запросов (создание или обновление)
                    // Читаем тело запроса (данные подзадачи в JSON) и преобразуем в строку
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(requestBody, Subtask.class); // Преобразуем JSON в объект Subtask
                    if (pathParts.length == 2) { // Если путь /subtasks - создание новой подзадачи
                        taskManager.addSubtask(subtask); // Добавляем подзадачу
                        sendText(exchange, "", 201);
                    } else if (pathParts.length == 3) { // Если путь /subtasks/{id} - обновление существующей подзадачи
                        int id = Integer.parseInt(pathParts[2]); // Извлекаем ID из пути
                        subtask.setId(id); // Устанавливаем ID подзадаче
                        taskManager.updateSubtask(subtask); // Обновляем подзадачу
                        sendText(exchange, "", 201);
                    }

                case "DELETE": // Обработка DELETE-запросов (удаление)
                    if (pathParts.length == 3) { // Если путь /subtasks/{id}
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteSubtask(id);
                        sendText(exchange, "", 200);
                    }
                    break;

                default:
                    sendText(exchange, "{\"error\": \"Метод не поддерживается\"}", 405);
            }
        } catch (NotFoundException e) { // Если подзадача не найдена
            sendNotFound(exchange, e.getMessage()); // Отправляем ошибку 404
        } catch (IllegalArgumentException e) { // Если данные некорректны
            sendHasInteractions(exchange, e.getMessage()); // Отправляем ошибку 406
        } catch (Exception e) { // При любой другой ошибке
            sendInternalError(exchange, String.format("Внутренняя ошибка сервера: %s", e.getMessage())); // Код 500
        }
    }
}
