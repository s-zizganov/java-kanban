package server;

import com.google.gson.Gson;
import entity.Status;
import entity.Subtask;
import entity.Epic;
import manager.*;
import manager.server.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Класс для тестирования эндпоинта /subtasks HTTP-сервера
public class HttpTaskServerSubtasksTest {
    private HttpTaskServer taskServer; // Сервер для тестирования
    private TaskManager taskManager; // Менеджер задач для управления данными
    private HistoryManager historyManager; // Менеджер истории просмотров
    private Gson gson; // Объект для работы с JSON
    private HttpClient client; // Клиент для отправки HTTP-запросов

    // Конструктор инициализирует все необходимые объекты
    public HttpTaskServerSubtasksTest() throws IOException {
        taskManager = Managers.getDefaultManger(); // Создаём менеджер задач
        historyManager = Managers.getDefaultHistory(); // Создаём менеджер истории
        taskServer = new HttpTaskServer(taskManager, historyManager); // Создаём сервер
        gson = HttpTaskServer.getGson(); // Получаем настроенный Gson
        client = HttpClient.newHttpClient(); // Создаём HTTP-клиент
    }

    // Метод выполняется перед каждым тестом
    @BeforeEach
    public void setUp() {
        taskServer.start(); // Запускаем сервер
    }

    // Метод выполняется после каждого теста
    @AfterEach
    public void shutDown() {
        taskServer.stop(); // Останавливаем сервер
    }

    // Тест на успешное добавление подзадачи
    @Test
    public void testAddSubtaskSuccess() throws IOException, InterruptedException {
        // Создаём эпик, к которому будет привязана подзадача
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        // Создаём подзадачу
        Subtask subtask = new Subtask("Test Subtask", "Description", Status.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask); // Преобразуем подзадачу в JSON

        // Создаём и отправляем POST-запрос
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson)) // Отправляем JSON в теле запроса
                .header("Content-Type", "application/json") // Указываем тип данных, JSON
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Получаем ответ

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Ожидался код ответа 201 Created");

        // Проверяем, что подзадача добавлена
        assertEquals(1, taskManager.getAllSubtask().size(), "Ожидалась одна подзадача");
        Subtask addedSubtask = taskManager.getAllSubtask().get(0);
        assertEquals("Test Subtask", addedSubtask.getName(), "Имя подзадачи не совпадает");
    }

    // Тест на успешное получение подзадачи по ID
    @Test
    public void testGetSubtaskByIdSuccess() throws IOException, InterruptedException {
        // Создаём эпик, к которому будет привязана подзадача
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        // Создаём подзадачу
        Subtask subtask = new Subtask("Test Subtask", "Description", Status.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now());
        taskManager.addSubtask(subtask); // Добавляем подзадачу


        // Создаём и отправляем GET-запрос
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Ожидался код ответа 200 OK");

        // Проверяем тело ответа
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask.getName(), responseSubtask.getName(), "Имя подзадачи не совпадает");
    }
}