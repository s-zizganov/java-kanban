package manager;

import entity.Task;

import java.util.ArrayList;

// Интерфейс для управления историей просмотров задач
public interface HistoryManager {
    // Метод добавляет задачу в историю просмотров
    void add(Task task);

    // Метод удаляет задачу из истории по её идентификатору (ID)
    void remove(int id);

    // Метод возвращает список всех задач, просмотренных ранее, в виде ArrayList
    ArrayList<Task> getHistory();
}