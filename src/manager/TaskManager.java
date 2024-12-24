package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;

import java.util.ArrayList;

public interface TaskManager {


    // Методы для работы с задачами
    Task addTask(Task task); //

    void deleteTask(int id); //

    Task getTask(int id); //

    ArrayList<Task> getAllTasks();//

    void clearTasks(); //

    void updateTask(Task task); //


    // Методы для работы с эпиками
    void addEpic(Epic epic); //

    void deleteEpic(int id); //

    Epic getEpic(int id); //

    ArrayList<Epic> getAllEpics(); //

    void clearEpics(); //

    void updateEpic(Epic epic); //


    // Методы для работы с подзадачами
    void addSubtask(Subtask subtask); //

    void deleteSubtask(int id); //

    Subtask getSubtask(int id); //

    ArrayList<Subtask> getSubtasksForEpic(int epicId); //

    void updateEpicStatus(Epic epic);

    //Обновление подзадачи
    void updateSubtask(Subtask subtask); //


    //Task getTaskById(Integer id);


}

