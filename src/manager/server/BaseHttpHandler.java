package manager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.server.adapters.DurationTypeAdapter;
import manager.server.adapters.LocalDateTimeTypeAdapter;

import java.time.Duration;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


// Абстрактный базовый класс для всех обработчиков HTTP-запросов.
// Реализует интерфейс HttpHandler и предоставляет общие методы для отправки ответов и обработки ошибок.
// Содержит общий экземпляр Gson для сериализации/десериализации JSON.

public abstract class BaseHttpHandler implements HttpHandler {
    protected final Gson gson; // Общий неизменяемый экземпляр Gson для всех подклассов

    // Конструктор базового обработчика.
    public BaseHttpHandler() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter()) // Регистрируем адаптер для Duration
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()) // Адаптер для LocalDateTime
                .create(); // Создаём настроенный экземпляр Gson
    }

    // Метод для отправки успешного ответа
    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8); // Преобразуем текст в байты в кодировке UTF-8
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8"); // Устанавливаем тип контента
        h.sendResponseHeaders(statusCode, resp.length); // Отправляем заголовки с кодом статуса и длиной
        h.getResponseBody().write(resp); // Записываем тело ответа
        h.close(); // Закрываем соединение
    }

    // Для отправки ответа в случае, если объект не был найден;
    public void sendNotFound(HttpExchange exchange, String message) throws IOException {
        String response = gson.toJson(new ErrorResponse(message)); // Формируем JSON с ошибкой
        sendText(exchange, response, 404); // Отправляем с кодом 404
    }

    // Для отправки ответа, если при создании или обновлении задача пересекается с уже существующими.
    public void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        String response = gson.toJson(new ErrorResponse(message));
        sendText(exchange, response, 406);
    }

    // Отправляет ответ с кодом 500 при внутренней ошибке.
    public void sendInternalError(HttpExchange exchange, String message) throws IOException {
        String response = gson.toJson(new ErrorResponse(message));
        sendText(exchange, response, 500);
    }


    // Вспомогательный класс для форматирования ошибок
    private static class ErrorResponse {
        private final String error; // Поле для сообщения об ошибке

        // Конструктор объекта ошибки.
        ErrorResponse(String error) {
            this.error = error;
        }

    }

}