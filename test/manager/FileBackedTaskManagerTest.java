package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;  // Временный файл для хранения данных во время тестов

    @BeforeEach // Метод выполняется перед каждым тестом. Создаёт временный файл и инициализирует FileBackedTaskManagerа
    public void setUp() throws IOException {
        tempFile = File.createTempFile("taskManagerTest", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
        // Метод выполняется после каждого теста. Удаляет временный файл, чтобы не засорять файловую систему.
    void tearDown() {
        tempFile.delete();
    }

    @Test
        // Тест для проверки сохранения и загрузки пустого менеджера
    void testSaveAndLoadEmptyManager() {
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пуст");
    }

    @Test
        // Тест для проверки загрузки из несуществующего файла
    void testLoadFromNonExistentFile() {
        File nonExistentFile = new File("nonexistent.csv"); // Указываем несуществующий файл
        // Проверяем, что загрузка вызывает исключение
        assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile),
                "Загрузка из несуществующего файла должна вызвать исключение");
    }

    @Test
        // Тест для проверки сохранения и загрузки данных
    void testSaveAndLoadWithData() {
        // Создаём обычную задачу
        Task task = new Task("Task1", "DescriptionT1", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task); // Добавляем задачу

        // Создаём эпик
        Epic epic = new Epic("Epic1", "DesccriptonE1");
        taskManager.addEpic(epic); // Добавляем эпик

        // Создаём подзадачу для эпика
        Subtask subtask = new Subtask("Sub1", "DescriptionS1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addSubtask(subtask); // Добавляем подзадачу

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile); // Загружаем данные из
        // файла в новый менеджер

        // Проверяем, что все объекты были загружены
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtask().size());

        // Проверяем задачу
        Task loadedTask = loadedManager.getTask(task.getId());
        assertEquals(task.getDuration(), loadedTask.getDuration());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());

        // Проверяем эпик
        Epic loadedEpic = loadedManager.getEpic(epic.getId());
        assertEquals(Duration.ofMinutes(30), loadedEpic.getDuration());
        assertEquals(LocalDateTime.of(2025, 3, 1, 11, 0), loadedEpic.getStartTime());
    }
}

