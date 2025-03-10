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

// Класс для тестирования эндпоинта /prioritized HTTP-сервера
public class HttpTaskServerPrioritizedTest {
    private HttpTaskServer taskServer;
    private TaskManager taskManager;
    private Gson gson;
    private HttpClient client;
    private HistoryManager historyManager;

    public HttpTaskServerPrioritizedTest() throws IOException {
        taskManager = Managers.getDefaultManger();
        historyManager = Managers.getDefaultHistory();
        taskServer = new HttpTaskServer(taskManager, historyManager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    public void setUp() {
        taskServer.start(); // Запуск сервера
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop(); // Остановка сервера после теста
    }

    // Тест на успешное получение списка задач, отсортированных по приоритету
    @Test
    public void testGetPrioritizedTasks_Success() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.now()); // Задача с текущим временем
        Task task2 = new Task("Task 2", "Description 2", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.now().plusHours(1)); // Задача с более поздним временем
        taskManager.addTask(task1); // Добавление первой задачи
        taskManager.addTask(task2); // Добавление второй задачи


        // Создаём и отправляем GET запрос
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); // Выполнение запроса


        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Ожидался код ответа 200 OK");

        // Проверяем количесвто и порядок
        Task[] prioritizedTasks = gson.fromJson(response.body(), Task[].class); // Десериализация списка задач
        assertEquals(2, prioritizedTasks.length,
                "Ожидалось две задачи в списке");// Проверка количества задач
        assertEquals(task1.getName(), prioritizedTasks[0].getName(),
                "Первая задача должна быть Task 1"); // Проверка порядка
        assertEquals(task2.getName(), prioritizedTasks[1].getName(),
                "Вторая задача должна быть Task 2"); // Проверка порядка
    }
}