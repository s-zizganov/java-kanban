package server;

import com.google.gson.Gson;
import entity.Status;
import entity.Task;
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

// Класс для тестирования эндпоинта /tasks HTTP-сервера
public class HttpTaskServerTasksTest {
    private HttpTaskServer taskServer;
    private TaskManager taskManager;
    private Gson gson;
    private HttpClient client;
    private HistoryManager historyManager;


    public HttpTaskServerTasksTest() throws IOException {
        taskManager = Managers.getDefaultManger();
        historyManager = Managers.getDefaultHistory();
        taskServer = new HttpTaskServer(taskManager, historyManager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
    }


    @BeforeEach
    public void setUp() {
        taskServer.start();
    }


    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    // Тест на успешное добавление задачи
    @Test
    public void testAddTask_Success() throws IOException, InterruptedException {
        // Создаём задачу
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        // Создаём и отправляем POST запрос
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Ожидался код ответа 201 Created");

        // Проверяем, что задача добавлена в менеджер
        assertEquals(1, taskManager.getAllTasks().size(), "Ожидалась одна задача в менеджере");
        Task addedTask = taskManager.getAllTasks().get(0);
        assertEquals("Test Task", addedTask.getName(), "Имя задачи не совпадает");
        assertEquals("Description", addedTask.getDescription(), "Описание задачи не совпадает");
    }


    // Тест для проверки, что возвращается код 500 при неверном формате данных.
    @Test
    public void testAddTask_InvalidJson() throws IOException, InterruptedException {
        // Отправляем некорректный JSON
        String invalidJson = "{invalid json}";

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(500, response.statusCode(), "Ожидался код ответа 500 Internal Server Error");
    }

    // Тест на успешное получение задачи по ID
    @Test
    public void testGetTaskById_Success() throws IOException, InterruptedException {
        // Добавляем задачу
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        // Создаём и отправляем GET запрос
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Ожидался код ответа 200 OK");

        // Проверяем тело ответа
        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task.getName(), responseTask.getName(), "Имя задачи не совпадает");
    }

    // Тест для проверки, что возвращается код 500 при несуществующем ID.
    @Test
    public void testGetTaskById_NotFound() throws IOException, InterruptedException {
        // Отправляем запрос с несуществующим ID
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(404, response.statusCode(), "Ожидался код ответа 404 Not Found");
    }

    // Тест на успешное удаление задачи
    @Test
    public void testDeleteTask_Success() throws IOException, InterruptedException {
        // Добавляем задачу
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        // Отправляем запрос на удаление
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Ожидался код ответа 200 No Content");

        // Проверяем, что задача удалена
        assertEquals(0, taskManager.getAllTasks().size(), "Ожидалось, что задач больше нет");
    }
}