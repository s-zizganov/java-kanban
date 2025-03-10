package manager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.*;
import manager.server.adapters.DurationTypeAdapter;
import manager.server.adapters.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

// Класс для запуска HTTP-сервера, который обрабатывает запросы к трекеру задач
public class HttpTaskServer {
    private static final int PORT = 8080; // Порт, на котором будет работать сервер
    private final HttpServer server; // Объект HTTP-сервера из стандартной библиотеки Java
    private final TaskManager taskManager; // Менеджер задач
    private final HistoryManager historyManager; // Менеджер истории для работы с историей просмотров


    // Конструктор с параметрами для создания сервера с заданными менеджерами
    public HttpTaskServer(TaskManager taskManager, HistoryManager historyManager) throws IOException {
        this.taskManager = taskManager; // Сохраняем переданный менеджер задач
        this.historyManager = historyManager; // Сохраняем переданный менеджер истории
        server = HttpServer.create(new InetSocketAddress(PORT), 0); // Создаём сервер на порту 8080
        // Регистрируем обработчики для разных эндпоинтов (URL):
        server.createContext("/tasks", new TaskHandler(taskManager)); // Для обычных задач
        server.createContext("/subtasks", new SubtaskHandler(taskManager)); // Для подзадач
        server.createContext("/epics", new EpicsHandler(taskManager)); // Для эпиков
        server.createContext("/history", new HistoryHandler(historyManager)); // Для истории просмотров
        server.createContext("/prioritized", new PrioritizedHandler(taskManager)); // Для приоритетного списка задач
    }


    // Конструктор по умолчанию (без параметров)
    public HttpTaskServer() throws IOException {
        this(Managers.getDefaultManger(), Managers.getDefaultHistory());

    }


    // Метод для создания объекта Gson с адаптерами
    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter()) // Адаптер для работы с Duration
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()) // Адаптер для LocalDateTime
                .create(); // Создаём и возвращаем настроенный Gson
    }


    // Метод для запуска сервера
    public void start() {
        System.out.println(String.format("Сервер запущен на порту %d", PORT)); // Сообщаем в консоль, что сервер работает
        server.start(); // Запускаем сервер
    }

    // Метод для остановки сервера
    public void stop() {
        server.stop(0); // Останавливаем сервер, 0 — немедленно
        System.out.println("Сервер остановлен");
    }


    // Точка входа в приложение
    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer(); // Создаём сервер с настройками по умолчанию
            server.start(); // Запускаем сервер
        } catch (IOException e) {
            System.out.println(String.format("Ошибка при запуске сервера: %s", e.getMessage())); // Ошибки запуска
        }
    }

}
