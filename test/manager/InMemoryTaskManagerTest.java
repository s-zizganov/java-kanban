package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void init() {
        taskManager = Managers.getDefaultManger();
    }

    //
    @Test
    void addTask() {
        String name = "Сделать ТЗ";
        String description = "Написать тесты";
        Task task = new Task(name, description, Status.NEW);

        Task createdTask = taskManager.addTask(task);

        Assertions.assertNotNull(createdTask.getId());
        Assertions.assertEquals(createdTask.getStatus(), Status.NEW);
        Assertions.assertEquals(createdTask.getDescription(), description);
        Assertions.assertEquals(createdTask.getName(), name);

    }


    // Проверка на равенство если равен их id
    @Test
    void testIdTask() {
        Task task1 = new Task("Таск 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Таск 2", "Описание 2", Status.IN_PROGRESS);

        task2.setId(task1.getId()); // Присваиваем одинаковый ID

        assertEquals(task1, task2);
    }


    // Проверка, что наследники класса Task равны друг другу, если их id равны
    @Test
    void epicsShouldBeEqualIfIdsAreEqual() {
        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic 2", "Description Epic 2");
        epic2.setId(1);

        // Проверка равенства эпиков по id
        Assertions.assertEquals(epic1, epic2, "Epics with the same ID should be equal.");
    }

    @Test
    void subtasksShouldBeEqualIfIdsAreEqual() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", Status.NEW, 2);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", Status.DONE, 2);
        subtask2.setId(1);

        // Проверка равенства подзадач по id
        Assertions.assertEquals(subtask1, subtask2, "Subtasks with the same ID should be equal.");
    }


    // Утилитарный класс всегда возвращает проинициализированный и готовый к работе экземпляр менеджера
    @Test
    void managersShouldReturnInitializedInstances() {

        assertNotNull(taskManager, "TaskManager instance should not be null.");
        assertTrue(taskManager instanceof InMemoryTaskManager, "Manager should be an instance of InMemoryTaskManager.");
    }

    @Test
    void testHistoryManagersReturnInitialized() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertTrue(historyManager instanceof InMemoryHistoryManager);
    }


    // проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void testAddDifferentTypes() {
        // Создаём задачи
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addTask(task);
        taskManager.addEpic(epic);

        // Устанавливаем правильный epicId для Subtask
        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);

        // Проверяем добавление
        Task receivedTask = taskManager.getTask(task.getId());
        Epic receivedEpic = taskManager.getEpic(epic.getId());
        Subtask receivedSubtask = taskManager.getSubtask(subtask.getId());

        // Проверяем, что задачи найдены
        assertNotNull(receivedTask);
        assertNotNull(receivedEpic);
        assertNotNull(receivedSubtask);

        // Проверяем содержимое задач
        assertEquals(task.getName(), receivedTask.getName());
        assertEquals(task.getDescription(), receivedTask.getDescription());
        assertEquals(task.getStatus(), receivedTask.getStatus());

        assertEquals(epic.getName(), receivedEpic.getName());
        assertEquals(epic.getDescription(), receivedEpic.getDescription());

        assertEquals(subtask.getName(), receivedSubtask.getName());
        assertEquals(subtask.getDescription(), receivedSubtask.getDescription());
        assertEquals(subtask.getStatus(), receivedSubtask.getStatus());
    }


    //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    @Test
    void testTaskWithCustomAndGeneratedId() {
        // Создаём задачу с заданным id
        Task customIdTask = new Task("Задача с заданным ID", "Описание 1", Status.NEW);
        customIdTask.setId(100); // Задаём id вручную
        taskManager.addTask(customIdTask);

        // Создаём задачу с автоматически сгенерированным id
        Task generatedIdTask = new Task("Автоматическое ID", "Описание 2", Status.NEW);
        taskManager.addTask(generatedIdTask); // id генерируется внутри менеджера

        // Проверяем, что обе задачи существуют
        Task receivedCustomIdTask = taskManager.getTask(customIdTask.getId());
        Task receivedGeneratedIdTask = taskManager.getTask(generatedIdTask.getId());

        // Проверяем, что задачи корректно добавлены и возвращаются по id
        assertNotNull(receivedCustomIdTask, "Задача с заданным id должна существовать");
        assertNotNull(receivedGeneratedIdTask, "Задача с сгенерированным id должна существовать");

        // Проверяем, что данные задач совпадают
        assertEquals(customIdTask.getName(), receivedCustomIdTask.getName());
        assertEquals(customIdTask.getDescription(), receivedCustomIdTask.getDescription());
        assertEquals(customIdTask.getStatus(), receivedCustomIdTask.getStatus());

        assertEquals(generatedIdTask.getName(), receivedGeneratedIdTask.getName());
        assertEquals(generatedIdTask.getDescription(), receivedGeneratedIdTask.getDescription());
        assertEquals(generatedIdTask.getStatus(), receivedGeneratedIdTask.getStatus());

        // Проверяем, что id задач разные
        assertNotEquals(customIdTask.getId(), generatedIdTask.getId(),
                "Id задач не должны конфликтовать (совпадать)");
    }


    // тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void taskShouldRemainUnchangedInTaskManager() {
        // Создаём новую задачу
        Task originalTask = new Task("Task 1", "Description 1", Status.NEW);

        // Добавляем задачу в менеджер
        taskManager.addTask(originalTask);

        // Получаем задачу из менеджера по её ID
        Task retrievedTask = taskManager.getTask(originalTask.getId());

        // Проверяем, что все поля задачи совпадают с изначальными
        Assertions.assertEquals(originalTask.getName(), retrievedTask.getName());
        Assertions.assertEquals(originalTask.getDescription(), retrievedTask.getDescription());
        Assertions.assertEquals(originalTask.getStatus(), retrievedTask.getStatus());
        Assertions.assertEquals(originalTask.getId(), retrievedTask.getId());
    }


    // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void historyManagerShouldPreserveTaskData() {

        HistoryManager historyManager = Managers.getDefaultHistory();
        // Создаём новую задачу
        Task task = new Task("Task 1", "Description 1", Status.NEW);

        // Добавляем задачу в историю через HistoryManager
        historyManager.add(task);

        // Получаем задачу из истории (это первая добавленная задача в списке)
        Task retrievedFromHistory = historyManager.getHistory().get(0);

        // Проверяем, что все поля задачи в истории совпадают с изначальными
        Assertions.assertEquals(task.getName(), retrievedFromHistory.getName());
        Assertions.assertEquals(task.getDescription(), retrievedFromHistory.getDescription());
        Assertions.assertEquals(task.getStatus(), retrievedFromHistory.getStatus());
        Assertions.assertEquals(task.getId(), retrievedFromHistory.getId());
    }
}





















