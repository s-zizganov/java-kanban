package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;
import entity.Status;
import exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager; // Переменная для менеджера истории
    private TaskManager taskManager; // Переменная для менеджера задач (нужна для создания задач с ID)

    @BeforeEach // Метод, который вызывается перед каждым тестом для инициализации объектов
    public void init() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefaultManger();
    }

    @Test
        // Тест для проверки добавления задачи в историю
    void add_standardCase() {
        Task task = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task); // Добавляем задачу в менеджер задач, чтобы получить ID
        historyManager.add(task); // Добавляем задачу в историю

        List<Task> history = historyManager.getHistory(); // Получаем список истории
        assertEquals(1, history.size()); // Проверяем, что в истории 1 задача
        assertEquals(task, history.get(0)); // Проверяем, что это наша задача
    }

    @Test
        // Тест для проверки удаления задачи из истории
    void remove_standardCase() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size()); // Проверяем, что осталась 1 задача
        assertEquals(task2, history.get(0)); // Проверяем, что это вторая задача
    }

    @Test
        // Тест для проверки получения списка истории
    void getHistory_standardCase() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task1); // Добавляем задачу
        historyManager.add(task1); // Добавляем задачу в историб

        List<Task> history = historyManager.getHistory(); // Получаем список истории
        assertEquals(1, history.size()); // Проверяем, что в истории 1 задача
    }


    @Test
        // Тест для проверки пустой истории
    void getHistory_emptyHistory() {
        List<Task> history = historyManager.getHistory(); // Получаем список истории
        assertTrue(history.isEmpty()); // Проверяем, что история пуста
    }

    @Test
        // Тест для проверки добавления дубликата в историю
    void add_duplicateTask() {
        Task task = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task); // Добавляем задачу

        historyManager.add(task); // Добавляем задачу в историю
        historyManager.add(task); // Добавляем задачу в историю еще раз

        List<Task> history = historyManager.getHistory(); //  Получаем список истории
        assertEquals(1, history.size()); // Проверяем, что в истории только 1 запись (дубликат не добавился)
    }

    @Test
        // Тест для проверки удаления задачи из начала истории
    void remove_fromBeginning() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId()); // Удаляем первую задачу (из начала)

        List<Task> history = historyManager.getHistory(); // Получаем список истории
        assertEquals(1, history.size()); // Проверяем, что осталась 1 задача
        assertEquals(task2, history.get(0)); // Проверяем, что это вторая задача
    }

    @Test
        // Тест для проверки удаления задачи из середины истории
    void remove_fromMiddle() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        Task task3 = new Task("Task3", "Desc", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2025, 3, 1, 12, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId()); // Удаляем вторую задачу (из середины)

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size()); // Проверяем, что осталось 2 задачи
        assertEquals(task1, history.get(0)); // Проверяем, что первая задача на месте
        assertEquals(task3, history.get(1)); // Проверяем, что третья задача на месте
    }

    @Test
        // Тест для проверки удаления задачи из конца истории
    void remove_fromEnd() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task2.getId()); // Удаляем вторую задачу (из конца)

        ArrayList<Task> history = historyManager.getHistory();
        assertEquals(1, history.size()); // Проверяем, что осталась 1 задача
        assertEquals(task1, history.get(0)); // Проверяем, что это первая задача
    }


    @Test
        // Тест на проверку, что после удаления подзадачи её ID больше не хранится в эпике
    void subtaskShouldNotHaveOldIdAfterDeletion() {
        // Создаём эпик
        Epic epic = new Epic("Epic1", "DesccriptonE1");
        taskManager.addEpic(epic); // Добавляем эпик

        // Создаём подзадачу для эпика
        Subtask subtask = new Subtask("Sub1", "DescriptionS1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addSubtask(subtask); // Добавляем подзадачу

        // Удаляем подзадачу
        int subtaskId = subtask.getId();
        taskManager.deleteSubtask(subtaskId);

        // Проверяем, что вызов getSubtask выбрасывает исключение
        assertThrows(NotFoundException.class, () -> taskManager.getSubtask(subtaskId),
                "Подзадача должна быть удалена, и getSubtask должен выбросить NotFoundException");
        // Проверяем, что ID подзадачи удален из эпика
        assertFalse(epic.getSubtaskIdList().contains(subtaskId));
    }


    @Test
        // Тест, что эпик не содержит ID удаленных подзадач.
    void epicShouldNotContainDeletedSubtaskIds() {
        // Создаём эпик
        Epic epic = new Epic("Epic1", "DesccriptonE1");
        taskManager.addEpic(epic); // Добавляем эпик

        // Создаём подзадачу для эпика
        Subtask subtask1 = new Subtask("Sub1", "DescriptionS1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        Subtask subtask2 = new Subtask("Sub1", "DescriptionS1", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 12, 0));
        taskManager.addSubtask(subtask1); // Добавляем подзадачу 1
        taskManager.addSubtask(subtask2); // Добавляем подзадачу 2

        // Удаляем первую подзадачу
        taskManager.deleteSubtask(subtask1.getId());

        // Проверяем, что ID первой подзадачи удален из эпика
        assertFalse(epic.getSubtaskIdList().contains(subtask1.getId()));
        // Проверяем, что ID второй подзадачи остался в эпике
        assertTrue(epic.getSubtaskIdList().contains(subtask2.getId()), "Эпик должен содержать ID оставшейся подзадачи.");
    }


    @Test
        // Тест на изменения полей задачи через сеттеры корректно сохраняются.
    void taskFieldsShouldBeConsistentAfterModification() {
        taskManager = Managers.getDefaultManger();

        Task task = new Task("Task 1", "Description 1", Status.NEW);
        taskManager.addTask(task);

        // Изменяем поля задачи через сеттеры
        task.setName("Updated Task 1");
        task.setDescription("Updated Description 1");
        task.setStatus(Status.IN_PROGRESS);

        // Получаем задачу из менеджера
        Task retrievedTask = taskManager.getTask(task.getId());

        // Проверяем, что поля задачи обновились
        assertEquals("Updated Task 1", retrievedTask.getName());
        assertEquals("Updated Description 1", retrievedTask.getDescription());
        assertEquals(Status.IN_PROGRESS, retrievedTask.getStatus());
    }
}