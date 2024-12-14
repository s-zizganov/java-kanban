package app;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import manager.TaskManager;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание 2", Status.IN_PROGRESS);
        Task task3 = new Task("Задача 3", "Описание 3", Status.DONE);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);


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


        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Эпики: " + taskManager.getAllEpics());
        System.out.println("Сабтаски эпика 1: " + taskManager.getSubtasksForEpic(epic1.getId()));
        System.out.println("Сабтаски эпика 2: " + taskManager.getSubtasksForEpic(epic2.getId()));
        System.out.println("Сабтаски эпика 3: " + taskManager.getSubtasksForEpic(epic3.getId()));
    }


}