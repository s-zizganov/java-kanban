package app;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new InMemoryTaskManager(); // Теперь для работы с таск менеджером используем
        // ссылку для работы с интерфейсом

        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        Task task3 = new Task("Задача 3", "Описание 3", Status.DONE);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);
        System.out.println("История: ");
        for (Task tasks : historyManager.getHistory()) {
            System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s",
                    tasks.getId(), tasks.getName(), tasks.getDescription(), tasks.getStatus()));
        }

        List<Task> history = historyManager.getHistory();
        System.out.println(history);


        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addEpic(epic3);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Сабтаск 1", "Описание сабтаска1", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Сабтаск 1", "Описание сабтаска1", Status.DONE, epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        Subtask subtask4 = new Subtask("Сабтаск 1", "Описание сабтаска1", Status.NEW, epic2.getId());
        Subtask subtask5 = new Subtask("Сабтаск 1", "Описание сабтаска1", Status.NEW, epic2.getId());
        Subtask subtask6 = new Subtask("Сабтаск 1", "Описание сабтаска1", Status.NEW, epic2.getId());
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtask6);

        Subtask subtask7 = new Subtask("Сабтаск 1", "Описание сабтаска1", Status.DONE, epic3.getId());
        Subtask subtask8 = new Subtask("Сабтаск 1", "Описание сабтаска1", Status.DONE, epic3.getId());
        Subtask subtask9 = new Subtask("Сабтаск 1", "Описание сабтаска1", Status.DONE, epic3.getId());
        taskManager.addSubtask(subtask7);
        taskManager.addSubtask(subtask8);
        taskManager.addSubtask(subtask9);


        // Вывод текущего состояния задач и эпиков с использованием String.format
        System.out.println("Задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s",
                    task.getId(), task.getName(), task.getDescription(), task.getStatus()));
        }

        System.out.println("\nЭпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s",
                    epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus()));
        }

        System.out.println("\nСабтаски эпика 1:");
        for (Subtask subtask : taskManager.getSubtasksForEpic(epic1.getId())) {
            System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s",
                    subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getStatus()));
        }

        System.out.println("\nСабтаски эпика 2:");
        for (Subtask subtask : taskManager.getSubtasksForEpic(epic2.getId())) {
            System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s",
                    subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getStatus()));
        }

        System.out.println("\nСабтаски эпика 3:");
        for (Subtask subtask : taskManager.getSubtasksForEpic(epic3.getId())) {
            System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s",
                    subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getStatus()));
        }


        // Обновление задачи
        task1.setDescription("Новое описание задачи 1");
        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);

        // Удаление задачи
        taskManager.deleteTask(task2.getId());

        // Удаление эпика
        taskManager.deleteEpic(epic1.getId());

        // Удаление сабтаска
        taskManager.deleteSubtask(subtask8.getId());


        // Итоговое состояние задач
        System.out.println("\n\n\n\nУдаление:");
        System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s", task2.getId(),
                task2.getName(), task2.getDescription(), task2.getStatus()));
        System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s", epic1.getId(),
                epic1.getName(), epic1.getDescription(), epic1.getStatus()));
        System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s", subtask8.getId(),
                subtask8.getName(), subtask8.getDescription(), subtask8.getStatus()));


        System.out.println("\nОбновление:");
        System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s", task1.getId(),
                task1.getName(), task1.getDescription(), task1.getStatus()));


        System.out.println("\n\n\n\nВсе задачи после обновлений и удалений: ");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s",
                    task.getId(), task.getName(), task.getDescription(), task.getStatus()));
        }

        System.out.println("\nВсе эпики после обновлений и удалений: ");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s",
                    epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus()));
        }

        System.out.println("\nВсе сабтаски после обновлений и удалений: ");
        for (Subtask subtask : taskManager.getAllSubtask()) {
            System.out.println(String.format(" - ID: %d, Название: %s, Описание: %s, Статус: %s",
                    subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getStatus()));
        }

    }
}