package manager.server;

import com.sun.net.httpserver.HttpExchange;
import entity.Task;
import exception.NotFoundException;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Класс-обработчик HTTP-запросов для работы с обычными задачами
public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager; // Менеджер задач для взаимодействия с данными


    // Конструктор принимает TaskManager
    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    // Метод обработки HTTP-запросов
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod(); // Получаем HTTP-метод (GET, POST, DELETE)
        String path = exchange.getRequestURI().getPath(); // Получаем путь запроса (например, /tasks или /tasks/1)
        String[] pathParts = path.split("/"); // Разбиваем путь на части по слешу

        try {
            switch (method) {
                case "GET": // Обработка GET-запросов
                    if (pathParts.length == 2) { // Если путь просто /tasks
                        // Получаем список всех задач и отправляем в формате JSON
                        String response = gson.toJson(taskManager.getAllTasks());
                        sendText(exchange, response, 200);
                    } else if (pathParts.length == 3) { // Если путь /tasks/{id}
                        int id = Integer.parseInt(pathParts[2]); // Извлекаем ID задачи из пути
                        Task task = taskManager.getTask(id); // Получаем задачу по ID
                        sendText(exchange, gson.toJson(task), 200); // Отправляем задачу в JSON
                    }
                    break;

                case "POST": // Обработка POST-запросов
                    // Читаем тело запроса (данные задачи в JSON) и преобразуем в строку
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(requestBody, Task.class); // Преобразуем JSON в объект Task
                    if (pathParts.length == 2) { // Если путь /tasks -= создание новой задачи
                        taskManager.addTask(task);
                        sendText(exchange, "", 201);
                    } else if (pathParts.length == 3) { // Если путь /tasks/{id} - обновление существующей задачи
                        int id = Integer.parseInt(pathParts[2]); // Извлекаем ID из пути
                        task.setId(id); // Устанавливаем ID задаче
                        taskManager.updateTask(task); // Обновляем задачу
                        sendText(exchange, "", 201);
                    }
                    break;

                case "DELETE": // Обработка DELETE-запросов (удаление)
                    if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteTask(id);
                        sendText(exchange, "", 200);
                    }
                    break;

                // Если метод не поддерживается
                default:
                    sendText(exchange, "{\"error\": \"Метод не поддерживается\"}", 405);
            }
        } catch (NotFoundException e) { // Если задача не найдена
            sendNotFound(exchange, e.getMessage()); // Отправляем ошибку 404
        } catch (IllegalArgumentException e) { // Если данные некорректны
            sendHasInteractions(exchange, e.getMessage()); // Отправляем ошибку 406
        } catch (Exception e) { // При любой другой ошибке
            sendInternalError(exchange, String.format("Внутренняя ошибка сервера: %s", e.getMessage())); // Код 500
        }
    }
}

