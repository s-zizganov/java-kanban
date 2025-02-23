package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Это абстрактный класс для тестирования всех методов интерфейса TaskManager
public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager; // Переменная для хранения тестируемого менеджера задач

    @BeforeEach // Метод, который должен быть реализован в подклассах для инициализации менеджера перед каждым тестом
    public abstract void setUp() throws IOException; // Абстрактный метод для инициализации конкретного менеджера


    @Test
        // Тест для проверки добавления обычной задачи
    void addTask_standardCase() {
        // Создаём задачу с названием, описанием, статусом, продолжительностью и временем начала
        Task task = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Task addedTask = taskManager.addTask(task); // Добавляем задачу в менеджер

        assertNotNull(addedTask.getId()); // Проверяем, что у задачи появился ID
        assertEquals(task.getName(), addedTask.getName()); // Проверяем, что название совпадает
        assertEquals(task.getDuration(), addedTask.getDuration()); // Проверяем, что продолжительность совпадает
        assertEquals(task.getStartTime(), addedTask.getStartTime()); // Проверяем, что время начала совпадает
    }

    @Test
        // Тест для проверки удаления задачи
    void deleteTask_standardCase() {
        Task task = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task);
        taskManager.deleteTask(task.getId());

        assertNull(taskManager.getTask(task.getId()));
    }

    @Test
        // Тест для проверки получения задачи по ID
    void getTask_standardCase() {
        Task task = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task);

        Task retrieved = taskManager.getTask(task.getId());
        assertEquals(task, retrieved);
    }

    @Test
        // Тест для проверки получения списка всех задач
    void getAllTasks_standardCase() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        ArrayList<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test
        // Тест для проверки очистки всех задач
    void clearTasks_standardCase() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task1);
        taskManager.clearTasks();

        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
        // Тест для проверки обновления задачи
    void updateTask_standardCase() {
        Task task = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task);
        task.setName("Updated");
        task.setDuration(Duration.ofMinutes(90));
        taskManager.updateTask(task);

        Task updated = taskManager.getTask(task.getId());
        assertEquals("Updated", updated.getName());
        assertEquals(Duration.ofMinutes(90), updated.getDuration());
    }

    @Test
        // Тест для проверки добавления эпика
    void addEpic_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);

        assertNotNull(epic.getId());
        assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @Test
        // Тест для проверки удаления эпика
    void deleteEpic_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addSubtask(subtask);
        taskManager.deleteEpic(epic.getId());

        assertNull(taskManager.getEpic(epic.getId()));
        assertNull(taskManager.getSubtask(subtask.getId()));
    }

    @Test
        // Тест для проверки получения эпика по ID
    void getEpic_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);

        assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @Test
        // Тест для проверки получения списка всех эпиков
    void getAllEpics_standardCase() {
        Epic epic1 = new Epic("Epic1", "Desc");
        Epic epic2 = new Epic("Epic2", "Desc");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        ArrayList<Epic> epics = taskManager.getAllEpics();
        assertEquals(2, epics.size());
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
    }

    @Test
        // Тест для проверки очистки всех эпиков
    void clearEpics_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        taskManager.clearEpics();

        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtask().size());
    }

    @Test
        // Тест для проверки обновления эпика
    void updateEpic_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        epic.setName("Updated Epic");
        taskManager.updateEpic(epic);

        Epic updated = taskManager.getEpic(epic.getId());
        assertEquals("Updated Epic", updated.getName());
    }

    @Test
        // Тест для проверки добавления подзадачи
    void addSubtask_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addSubtask(subtask);

        assertNotNull(subtask.getId());
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
        assertTrue(epic.getSubtaskIdList().contains(subtask.getId()));
    }

    @Test
        // Тест для проверки удаления подзадачи
    void deleteSubtask_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addSubtask(subtask);
        taskManager.deleteSubtask(subtask.getId());

        assertNull(taskManager.getSubtask(subtask.getId()));
        assertFalse(epic.getSubtaskIdList().contains(subtask.getId()));
    }

    @Test
        // Тест для проверки получения подзадачи по ID
    void getSubtask_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
        // Тест для проверки получения списка подзадач эпика
    void getSubtasksForEpic_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Subtask subtask2 = new Subtask("Sub2", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        ArrayList<Subtask> subtasks = taskManager.getSubtasksForEpic(epic.getId());
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
    }

    @Test
        // Тест для проверки получения списка всех подзадач
    void getAllSubtask_standardCase() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addSubtask(subtask1);

        ArrayList<Subtask> subtasks = taskManager.getAllSubtask();
        assertEquals(1, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
    }

    @Test
        // Тест для проверки получения отсортированного списка задач
    void getPrioritizedTasks_standardCase() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 12, 0));
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks(); // Получаем отсортированный список
        assertEquals(2, prioritized.size()); // Проверяем, что в списке 2 задачи
        assertEquals(task2, prioritized.get(0)); // Проверяем, что первая задача — Task2 (раньше по времени)
        assertEquals(task1, prioritized.get(1)); // Проверяем, что вторая задача — Task1
    }


    @Test
        // Тест для проверки расчёта статуса эпика, когда все подзадачи NEW
    void updateEpicStatus_allNew() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Subtask subtask2 = new Subtask("Sub2", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.updateEpicStatus(epic);
        assertEquals(Status.NEW, epic.getStatus()); // Проверяем, что статус NEW
    }

    @Test
        // Тест для проверки расчёта статуса эпика, когда все подзадачи DONE
    void updateEpicStatus_allDone() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.DONE, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Subtask subtask2 = new Subtask("Sub2", "Desc", Status.DONE, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.updateEpicStatus(epic);
        assertEquals(Status.DONE, epic.getStatus()); // Проверяем, что статус DONE
    }

    @Test
        // Тест для проверки расчёта статуса эпика, когда подзадачи NEW и DONE
    void updateEpicStatus_newAndDone() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Subtask subtask2 = new Subtask("Sub2", "Desc", Status.DONE, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.updateEpicStatus(epic); // Обновляем статус эпика
        assertEquals(Status.NEW, epic.getStatus()); // Проверяем, что статус NEW
    }

    @Test
        // Тест для проверки расчёта статуса эпика, когда есть подзадача IN_PROGRESS
    void updateEpicStatus_inProgress() {
        Epic epic = new Epic("Epic1", "Desc");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.IN_PROGRESS, epic.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        Subtask subtask2 = new Subtask("Sub2", "Desc", Status.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.updateEpicStatus(epic); // Обновляем статус эпика
        assertEquals(Status.IN_PROGRESS, epic.getStatus()); // Проверяем, что статус IN_PROGRESS
    }


    @Test
        // Тест для проверки пересечения задач по времени
    void testTaskOverlap() {
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 1, 10, 0));
        taskManager.addTask(task1); // Добавляем первую задачу

        // Проверяем, что добавление пересекающейся задачи вызывает исключение
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 10, 30));
        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2),
                "Пересекающаяся задача должна вызвать исключение");

        // Проверяем, что непересекающаяся задача добавляется
        Task task3 = new Task("Task3", "Desc", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 1, 11, 0));
        assertDoesNotThrow(() -> taskManager.addTask(task3),
                "Непересекающаяся задача должна добавляться без исключений");
    }
}