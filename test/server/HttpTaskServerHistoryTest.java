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

// Класс для тестирования эндпоинта "/history" HTTP-сервера
public class HttpTaskServerHistoryTest {
    private HttpTaskServer taskServer;
    private TaskManager taskManager;
    private Gson gson;
    private HttpClient client;
    private HistoryManager historyManager;

    // Конструктор
    public HttpTaskServerHistoryTest() throws IOException {
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

    // Тест на успешное получение истории просмотров
    @Test
    public void testGetHistorySuccess() throws IOException, InterruptedException {
        // создаем задачу
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);
        historyManager.add(task); // Добавляем в историю

        // Создаём и отправляем GET запрос
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Ожидался код ответа 200 OK");

        // Проверяем, что история возвращена
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertEquals(1, history.length, "Ожидалась одна задача в истории");
        assertEquals(task.getName(), history[0].getName(), "Имя задачи в истории не совпадает");
    }
}