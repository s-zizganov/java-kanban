package manager;

import entity.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

//Тесты для FileBackedTaskManager. Проверяют корректность сохранения и загрузки задач из файла.
class FileBackedTaskManagerTest {
    private File tempFile; // Временный файл для хранения данных менеджера задач  
    private FileBackedTaskManager manager; // Менеджер задач, который будем тестировать  


    // Метод выполняется перед каждым тестом. Создаёт временный файл и инициализирует FileBackedTaskManager
    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("taskManagerTest", ".csv"); // Создаём временный файл для тестов  
        manager = new FileBackedTaskManager(tempFile); // Создаём новый экземпляр менеджера с этим файлом
    }


    // Метод выполняется после каждого теста. Удаляет временный файл, чтобы не засорять файловую систему.
    @AfterEach
    void tearDown() {
        tempFile.delete();
    }


    // Проверяет, что пустой менеджер корректно сохраняется и загружается.
    @Test
    void testSaveAndLoadEmptyManager() {
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(manager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
    }


    // Проверяет сохранение и загрузку нескольких задач, эпиков и подзадач.
    @Test
    void testSaveAndLoadMultipleTasks() throws IOException {
        // Создаём задачу и добавляем в менеджер  
        Task task1 = new Task("Task1", "Description1", Status.NEW);
        Task task2 = new Task("Task2", "Description2", Status.NEW);
        manager.addTask(task1);
        manager.addTask(task2);
        System.out.println("Файл после сохранения:\n " + Files.readString(tempFile.toPath()) + "\n");

        // Создаём эпик и добавляем в менеджер  
        Epic epic1 = new Epic("Epic1", "EpicDescription1");
        Epic epic2 = new Epic("Epic2", "EpicDescription2");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        System.out.println("Файл после сохранения:\n " + Files.readString(tempFile.toPath()) + "\n");

        // Создаём подзадачу, связанную с эпиком, и добавляем в менеджер  
        Subtask subtask1 = new Subtask("Subtask1", "SubtaskDescription1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "SubtaskDescription2", Status.NEW, epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        System.out.println("Файл после сохранения:\n " + Files.readString(tempFile.toPath()) + "\n");

        // Загружаем менеджер из файла  
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);


        System.out.println("Оригинальный ID: " + task1.getId());
        System.out.println("Загруженный ID: " + loadedManager.getTask(task1.getId()));

        System.out.println("Оригинальный ID: " + task2.getId());
        System.out.println("Загруженный ID: " + loadedManager.getTask(task2.getId()));

        System.out.println("Оригинальный ID: " + epic1.getId());
        System.out.println("Загруженный ID: " + loadedManager.getEpic(epic1.getId()));

        System.out.println("Оригинальный ID: " + epic2.getId());
        System.out.println("Загруженный ID: " + loadedManager.getEpic(epic2.getId()));

        System.out.println("Оригинальный ID: " + subtask1.getId());
        System.out.println("Загруженный ID: " + loadedManager.getSubtask(subtask1.getId()));

        System.out.println("Оригинальный ID: " + subtask2.getId());
        System.out.println("Загруженный ID: " + loadedManager.getSubtask(subtask2.getId()));

        // Проверяем, что все задачи загружены корректно  
        assertEquals(2, loadedManager.getAllTasks().size(), "Неверное количество задач");
        assertEquals(2, loadedManager.getAllEpics().size(), "Неверное количество эпиков");
        assertEquals(2, loadedManager.getAllSubtask().size(), "Неверное количество подзадач");

        // Проверяем, что загруженные задачи совпадают с исходными  
        assertEquals(task1, loadedManager.getTask(task1.getId()), "Задача не совпадает");
        assertEquals(epic1, loadedManager.getEpic(epic1.getId()), "Эпик не совпадает");
        assertEquals(subtask1, loadedManager.getSubtask(subtask1.getId()), "Подзадача не совпадает");
    }
}
