package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    public void init() {
        historyManager = Managers.getDefaultHistory();
    }


    @Test // задачи корректно добавляются в историю и сохраняются в правильном порядке.
    void historyManagerShouldCorrectlyAddTasks() {
        taskManager = Managers.getDefaultManger();

        Task task1 = new Task("Task1", "Description1", Status.NEW);
        Task task2 = new Task("Task2", "Description2", Status.IN_PROGRESS);

        // Добавляем в менеджер для назначения id
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Добавляем задачи в историю
        historyManager.add(task1);
        historyManager.add(task2);

        System.out.println("Task1 ID: " + task1.getId());
        System.out.println("Task2 ID: " + task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());

        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));

    }


    @Test // Задачи корректно удаляются из истории.
    void historyManagerShouldRemoveTasksCorrectly() {
        taskManager = Managers.getDefaultManger();

        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);

        // Добавляем в менеджер для назначения id
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Добавляем задачи в историю
        historyManager.add(task1);
        historyManager.add(task2);


        System.out.println("Task1 ID: " + task1.getId());
        System.out.println("Task2 ID: " + task2.getId());

        System.out.println("До удаления: " + historyManager.getHistory());
        // Удаляем первую задачу
        historyManager.remove(task1.getId());
        System.out.println("После удаления: " + historyManager.getHistory());
        // Получаем историю
        List<Task> history = historyManager.getHistory();

        // Проверяем, что история содержит только вторую задачу
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }


    @Test //  история не содержит дубликатов задач.+++
    void historyManagerShouldNotAllowDuplicates() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Task 1", "Description 1", Status.NEW);

        // Добавляем задачу дважды
        historyManager.add(task);
        historyManager.add(task); // Повторное добавление

        // Получаем историю
        List<Task> history = historyManager.getHistory();

        // Проверяем, что история содержит только одну задачу
        assertEquals(1, history.size());
    }

  @Test// после удаления подзадачи её ID больше не хранится в эпике ++++
    void subtaskShouldNotHaveOldIdAfterDeletion() {
        taskManager = Managers.getDefaultManger();

        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask);

        // Удаляем подзадачу
        int subtaskId = subtask.getId();
        taskManager.deleteSubtask(subtaskId);

        // Проверяем, что подзадача удалена
        assertNull(taskManager.getSubtask(subtaskId), "Подзадача должна быть удалена.");
        // Проверяем, что ID подзадачи удален из эпика
        assertFalse(epic.getSubtaskIdList().contains(subtaskId));
    }

    @Test // эпик не содержит ID удаленных подзадач. ++++
    void epicShouldNotContainDeletedSubtaskIds() {
        taskManager = Managers.getDefaultManger();

        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.NEW, epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        // Удаляем первую подзадачу
        taskManager.deleteSubtask(subtask1.getId());

        // Проверяем, что ID первой подзадачи удален из эпика
        assertFalse(epic.getSubtaskIdList().contains(subtask1.getId()));
        // Проверяем, что ID второй подзадачи остался в эпике
        assertTrue(epic.getSubtaskIdList().contains(subtask2.getId()), "Эпик должен содержать ID оставшейся подзадачи.");
    }

    @Test // изменения полей задачи через сеттеры корректно сохраняются. ++++
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













































