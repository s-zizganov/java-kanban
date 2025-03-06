package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach // Метод, который вызывается перед каждым тестом для инициализации менеджера
    public void setUp() {
        taskManager = Managers.getDefaultManger(); // Инициализация taskManager
    }


    @Test
        // Тест для проверки добавления задачи
    void addTask() {
        String name = "Сделать ТЗ";
        String description = "Написать тесты";
        Task task = new Task(name, description, Status.NEW);

        Task createdTask = taskManager.addTask(task);

        assertNotNull(createdTask.getId());
        assertEquals(Status.NEW, createdTask.getStatus());
        assertEquals(description, createdTask.getDescription());
        assertEquals(name, createdTask.getName());
    }

    @Test
        // Тест для проверки равенства задач по ID
    void testIdTask() {
        Task task1 = new Task("Таск 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Описание 2", Status.IN_PROGRESS);

        taskManager.addTask(task1); // Добавляем задачу, чтобы получить ID
        task2.setId(task1.getId()); // Присваиваем одинаковый ID

        assertEquals(task1, task2);
    }

    @Test
        // Тест для проверки равенства эпиков по ID
    void epicsShouldBeEqualIfIdsAreEqual() {
        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");
        epic2.setId(1);

        assertEquals(epic1, epic2, "Epics with the same ID should be equal.");
    }

    @Test
        // Тест для проверки равенства подзадач по ID
    void subtasksShouldBeEqualIfIdsAreEqual() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", Status.NEW, 2);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", Status.DONE, 2);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Subtasks with the same ID should be equal.");
    }

    @Test
        // Тест для проверки инициализации менеджера задач
    void managersShouldReturnInitializedInstances() {
        assertNotNull(taskManager, "TaskManager instance should not be null.");
        assertTrue(taskManager instanceof InMemoryTaskManager, "Manager should be an instance of InMemoryTaskManager.");
    }

    @Test
        // Тест для проверки инициализации менеджера истории
    void testHistoryManagersReturnInitialized() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }

    @Test
        // Тест для проверки добавления разных типов задач
    void testAddDifferentTypes() {
        // Создаём задачи с временем и продолжительностью
        Task task = new Task("Task 1", "Description 1", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addTask(task);
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addSubtask(subtask);

        // Получаем добавленные объекты из менеджера
        Task receivedTask = taskManager.getTask(task.getId());
        Epic receivedEpic = taskManager.getEpic(epic.getId());
        Subtask receivedSubtask = taskManager.getSubtask(subtask.getId());

        // Проверяем, что все объекты были добавлены
        assertNotNull(receivedTask);
        assertNotNull(receivedEpic);
        assertNotNull(receivedSubtask);

        // Проверяем поля задач
        assertEquals(task.getName(), receivedTask.getName());
        assertEquals(task.getDescription(), receivedTask.getDescription());
        assertEquals(task.getStatus(), receivedTask.getStatus());
        assertEquals(task.getDuration(), receivedTask.getDuration());
        assertEquals(task.getStartTime(), receivedTask.getStartTime());

        assertEquals(epic.getName(), receivedEpic.getName());
        assertEquals(epic.getDescription(), receivedEpic.getDescription());

        assertEquals(subtask.getName(), receivedSubtask.getName());
        assertEquals(subtask.getDescription(), receivedSubtask.getDescription());
        assertEquals(subtask.getStatus(), receivedSubtask.getStatus());
        assertEquals(subtask.getDuration(), receivedSubtask.getDuration());
        assertEquals(subtask.getStartTime(), receivedSubtask.getStartTime());
    }

    @Test
        // Тест для проверки работы задач с заданным и сгенерированным ID
    void testTaskWithCustomAndGeneratedId() {
        // Создаём задачу с заданным ID
        Task customIdTask = new Task("Задача с заданным ID", "Описание 1", Status.NEW);
        customIdTask.setId(100);
        taskManager.addTask(customIdTask);

        // Создаём задачу с автоматически сгенерированным ID
        Task generatedIdTask = new Task("Автоматическое ID", "Описание 2", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(generatedIdTask); // Добавляем задачу, ID генерируется менеджером

        // Получаем обе задачи из менеджера
        Task receivedCustomIdTask = taskManager.getTask(customIdTask.getId());
        Task receivedGeneratedIdTask = taskManager.getTask(generatedIdTask.getId());

        // Проверяем, что обе задачи существуют
        assertNotNull(receivedCustomIdTask, "Задача с заданным id=" + receivedCustomIdTask.getId() +
                " должна существовать");
        assertNotNull(receivedGeneratedIdTask, "Задача с сгенерированным id=" + receivedGeneratedIdTask.getId() +
                " должна существовать");

        // Проверяем поля задачи с заданным ID
        assertEquals(customIdTask.getName(), receivedCustomIdTask.getName());
        assertEquals(customIdTask.getDescription(), receivedCustomIdTask.getDescription());
        assertEquals(customIdTask.getStatus(), receivedCustomIdTask.getStatus());

        // Проверяем поля задачи со сгенерированным ID

        assertEquals(generatedIdTask.getName(), receivedGeneratedIdTask.getName());
        assertEquals(generatedIdTask.getDescription(), receivedGeneratedIdTask.getDescription());
        assertEquals(generatedIdTask.getStatus(), receivedGeneratedIdTask.getStatus());
        assertEquals(generatedIdTask.getDuration(), receivedGeneratedIdTask.getDuration());

        // Проверяем, что ID задач разные
        assertNotEquals(customIdTask.getId(), generatedIdTask.getId(),
                "Id задач не должны конфликтовать (совпадать)");
    }

    @Test
        // Тест для проверки неизменности задачи после добавления в менеджер
    void taskShouldRemainUnchangedInTaskManager() {
        // Создаём задачу с временем и продолжительностью
        Task originalTask = new Task("Task 1", "Description 1", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(originalTask); // Добавляем задачу в менеджер


        Task retrievedTask = taskManager.getTask(originalTask.getId());  // Получаем задачу из менеджера

        // Проверяем, что все поля остались неизменными
        assertEquals(originalTask.getName(), retrievedTask.getName());
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription());
        assertEquals(originalTask.getStatus(), retrievedTask.getStatus());
        assertEquals(originalTask.getId(), retrievedTask.getId());
        assertEquals(originalTask.getDuration(), retrievedTask.getDuration());
        assertEquals(originalTask.getStartTime(), retrievedTask.getStartTime());
    }

    @Test
        // Тест для проверки сохранения данных задачи в истории
    void historyManagerShouldPreserveTaskData() {
        HistoryManager historyManager = Managers.getDefaultHistory(); // Создаём менеджер истории
        // Создаём задачу с временем и продолжительностью:
        Task task = new Task("Task 1", "Description 1", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));

        historyManager.add(task); // Добавляем задачу в историю

        Task retrievedFromHistory = historyManager.getHistory().get(0); // Получаем задачу из истории

        // Проверяем, что все поля задачи в истории совпадают с исходными
        assertEquals(task.getName(), retrievedFromHistory.getName());
        assertEquals(task.getDescription(), retrievedFromHistory.getDescription());
        assertEquals(task.getStatus(), retrievedFromHistory.getStatus());
        assertEquals(task.getId(), retrievedFromHistory.getId());
        assertEquals(task.getDuration(), retrievedFromHistory.getDuration());
        assertEquals(task.getStartTime(), retrievedFromHistory.getStartTime());
    }
}