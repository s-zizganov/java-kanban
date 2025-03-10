package manager;

import entity.Epic;
import entity.Subtask;
import entity.Task;
import exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;

// Интерфейс для управления задачами, эпиками и подзадачами
public interface TaskManager {


    // Методы для работы с задачами
    Task addTask(Task task);

    void deleteTask(int id);

    Task getTask(int id) throws NotFoundException; // 9. Обновили, чтобы выбрасывал исключение

    ArrayList<Task> getAllTasks();

    void clearTasks();

    void updateTask(Task task);


    // Методы для работы с эпиками
    void addEpic(Epic epic);

    void deleteEpic(int id);

    Epic getEpic(int id) throws NotFoundException; // 9. Обновили, чтобы выбрасывал исключение

    ArrayList<Epic> getAllEpics();

    void clearEpics();

    void updateEpic(Epic epic);


    // Методы для работы с подзадачами
    void addSubtask(Subtask subtask);

    void deleteSubtask(int id);

    Subtask getSubtask(int id) throws NotFoundException; // 9. Обновили, чтобы выбрасывал исключение

    ArrayList<Subtask> getSubtasksForEpic(int epicId); //

    void updateEpicStatus(Epic epic);

    void updateSubtask(Subtask subtask);

    ArrayList<Subtask> getAllSubtask();

    // Новый метод для получения отсортированного списка задач
    List<Task> getPrioritizedTasks();
}
