package server;

import com.google.gson.Gson;
import entity.Epic;
import entity.Status;
import entity.Subtask;
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

// Класс для тестирования эндпоинта /epics HTTP-сервера
public class HttpTaskServerEpicsTest {
    private HttpTaskServer taskServer; // Сервер для тестирования
    private TaskManager taskManager; // Менеджер задач для управления данными
    private HistoryManager historyManager; // Менеджер истории просмотров
    private Gson gson; // Объект для работы с JSON
    private HttpClient client; // Клиент для отправки HTTP-запросов

    // Конструктор инициализирует все необходимые объекты
    public HttpTaskServerEpicsTest() throws IOException {
        taskManager = Managers.getDefaultManger();
        historyManager = Managers.getDefaultHistory();
        taskServer = new HttpTaskServer(taskManager, historyManager); // Создаём сервер
        gson = HttpTaskServer.getGson(); // Получаем настроенный Gson
        client = HttpClient.newHttpClient(); // Создаём HTTP-клиент
    }

    // Метод выполняется перед каждым тестом
    @BeforeEach
    public void setUp() {
        taskServer.start();
    }

    // Метод выполняется после каждого теста
    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    // Тест на успешное добавление эпика
    @Test
    public void testAddEpicSuccess() throws IOException, InterruptedException {
        // Создаём эпик
        Epic epic = new Epic("Test Epic", "Description");
        String epicJson = gson.toJson(epic);

        // Создаём и отправляем POST запрос
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson)) // Отправляем JSON в теле запроса
                .header("Content-Type", "application/json") // Указываем тип данных JSON
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Получаем ответ

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Ожидался код ответа 201 Created");

        // Проверяем, что эпик добавлен
        assertEquals(1, taskManager.getAllEpics().size(), "Ожидался один эпик");
        Epic addedEpic = taskManager.getAllEpics().get(0);
        assertEquals("Test Epic", addedEpic.getName(), "Имя эпика не совпадает");
    }

    // Тест на успешное получение подзадач эпика
    @Test
    public void testGetSubtasksForEpicSuccess() throws IOException, InterruptedException {
        // Создаём эпик
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.addEpic(epic);

        // Создаём подзадачу
        Subtask subtask = new Subtask("Test Subtask", "Description", Status.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.now());
        taskManager.addSubtask(subtask);

        // Отправляем запрос
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Ожидался код ответа 200 OK");

        // Проверяем, что подзадача возвращена
        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(1, subtasks.length, "Ожидалась одна подзадача");
        assertEquals("Test Subtask", subtasks[0].getName(), "Имя подзадачи не совпадает");
    }
}