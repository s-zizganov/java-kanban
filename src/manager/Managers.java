package manager;

import java.io.File;

// Утилитарный класс для создания объектов менеджеров
public class Managers {

    // Метод для создания менеджера задач, работающего в памяти
    public static InMemoryTaskManager getDefaultManger() {
        return new InMemoryTaskManager();
    }

    // Метод для создания менеджера истории, работающего в памяти
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    // Метод для создания менеджера задач с сохранением в файл
    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }
}