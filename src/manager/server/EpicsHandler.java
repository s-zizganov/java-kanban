package manager.server;

import com.sun.net.httpserver.HttpExchange;
import entity.Epic;
import entity.Subtask;
import exception.NotFoundException;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Класс-обработчик HTTP-запросов для работы с эпиками (группами задач)
public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager; // Менеджер задач для взаимодействия с данными

    // Конструктор принимает менеджер задач для работы с эпиками
    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    // Метод обработки HTTP-запросов
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod(); // Получаем метод запроса (GET, POST, DELETE)
        String path = exchange.getRequestURI().getPath(); // Получаем путь запроса (например, /epics или /epics/1)
        String[] pathParts = path.split("/"); // Разбиваем путь на части по слэшу (/)

        try {
            switch (method) {
                case "GET": // Обработка GET-запросов (получение данных)
                    if (pathParts.length == 2) { // Если путь просто /epics
                        // Получаем список всех эпиков и отправляем в формате JSON
                        String response = gson.toJson(taskManager.getAllEpics());
                        sendText(exchange, response, 200); // Отправляем эпик в JSON
                    } else if (pathParts.length == 3) { // Если путь /epics/{id}
                        int id = Integer.parseInt(pathParts[2]); // Извлекаем ID эпика из пути
                        Epic epic = taskManager.getEpic(id); // Получаем эпик по ID
                        sendText(exchange, gson.toJson(epic), 200); // Отправляем эпик в JSON
                    } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) { // Если путь /epics/{id}/subtasks
                        int id = Integer.parseInt(pathParts[2]); // Извлекаем ID эпика
                        List<Subtask> subtasks = taskManager.getSubtasksForEpic(id); // Получаем подзадачи эпика
                        sendText(exchange, gson.toJson(subtasks), 200); // Отправляем список подзадач в JSON
                    } else {
                        // Если путь некорректен, отправляем ошибку 404
                        sendText(exchange, "{\"error\": \"Неверный путь\"}", 404);
                    }
                    break;
                case "POST": // Обработка POST-запросов (создание или обновление)
                    // Читаем тело запроса (данные эпика в JSON) и преобразуем в строку
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(requestBody, Epic.class); // Преобразуем JSON в объект Epic
                    if (pathParts.length == 2) { // Если путь /epics - создание нового эпика
                        taskManager.addEpic(epic); // Добавляем эпик
                        sendText(exchange, "", 201);
                    } else if (pathParts.length == 3) { // Если путь /epics/{id} - обновление существующего эпика
                        int id = Integer.parseInt(pathParts[2]); // Извлекаем ID из пути
                        epic.setId(id); // Устанавливаем ID эпику
                        taskManager.updateEpic(epic); // Обновляем эпик
                        sendText(exchange, "", 201);
                    }
                    break;

                case "DELETE": // Обработка DELETE-запросов (удаление)
                    if (pathParts.length == 3) { // Если путь "/epics/{id}"
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteEpic(id);
                        sendText(exchange, "", 200);
                    }
                    break;

                // Если метод не поддерживается
                default:
                    sendText(exchange, "{\"error\": \"Метод не поддерживается\"}", 405);
            }
        } catch (NotFoundException e) { // Если эпик или подзадача не найдены
            sendNotFound(exchange, e.getMessage()); // Отправляем ошибку 404
        } catch (IllegalArgumentException e) { // Если данные некорректны
            sendHasInteractions(exchange, e.getMessage()); // Отправляем ошибку 406
        } catch (Exception e) { // При любой другой ошибке
            sendInternalError(exchange, "Внутрення ошибка сервера: " + e.getMessage()); // Код 500 - ошибка серв.
        }
    }
}
