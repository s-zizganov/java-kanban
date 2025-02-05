package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import exception.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/*
Класс FileBackedTaskManager добавляет функциональность
для сохранения и загрузки задач, эпиков и подзадач в файл.
 */

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file; // Файл, в который будут сохраняться данные

    public FileBackedTaskManager(File file) { // Конструктор, принимающий файл для сохранения данных.
        this.file = file;
    }


    // Метод для загрузки данных из файла и создания экземпляра FileBackedTaskManager
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            System.out.println("Загрузка из файла: " + file.getPath());
            br.readLine(); // Пропускаем заголовок файла

            int maxID = 0; // Переменная для отслеживания максимального ID задачи

            // Чтение файла построчно
            while (br.ready()) {
                String line = br.readLine();
                // System.out.println("Прочитан строка: " + line); // Лог
                if (line.isEmpty()) {
                    continue; // Пропускаем пустые строки
                }

                // Преобразуем строку в объект Task
                Task task = fromString(line);
                int taskID = task.getId();

                // В зависимости от типа задачи добавляем её в соответствующую коллекцию
                switch (task.getType()) {
                    case TASK_TYPE:
                        fileBackedTaskManager.tasks.put(taskID, task);
                        break;
                    case EPIC_TYPE:
                        fileBackedTaskManager.epics.put(taskID, (Epic) task);
                        break;
                    case SUBTASK_TYPE:
                        fileBackedTaskManager.subtasks.put(taskID, (Subtask) task);
                        break;
                }
            }

            // System.out.println("Все задачи загружены."); // Лог
            // System.out.println("Загруженные задачи: " + fileBackedTaskManager.getAllTasks().size()); // Лог
            // System.out.println("Загруженные эпики: " + fileBackedTaskManager.getAllEpics().size()); // Лог
            // System.out.println("Загруженные подзадачи: " + fileBackedTaskManager.getAllSubtask().size()); // Лог

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла" + e.getMessage());
        }

        return fileBackedTaskManager;
    }

    // Метод преобразует объект Task в строку для сохранения в файл.
    private static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                (task instanceof Subtask ? ((Subtask) task).getEpicId() : "") // Для подзадачи добавляем epicId
        );
    }

    // Преобразует строку из файла в объект Task
    public static Task fromString(String value) {
        // System.out.println("Парсим строку: " + value); // Логирование
        String[] params = value.split(",");

        if (params.length < 5) {
            System.out.println("Ошибка: некорректный формат строки -> " + value);
            return null;
        }

        int id = Integer.parseInt(params[0]);
        String type = params[1];
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Некорректный формат строки: отсутствует тип задачи -> " + value);
        }
        String name = params[2];
        Status status = Status.valueOf(params[3]);
        String description = params[4];

        // В зависимости от типа задачи создаем соответствующий объект
        switch (type) {
            case "EPIC_TYPE":
                Epic epic = new Epic(name, description);
                epic.setId(Integer.parseInt(params[0]));
                epic.setStatus(Status.valueOf(params[3]));
                epic.setTypeTask(TaskType.EPIC_TYPE); // Устанавливаем тип
                return epic;

            case "TASK_TYPE":
                Task task = new Task(name, description, status);
                task.setId(id);
                task.setTypeTask(TaskType.TASK_TYPE);
                return task;

            case "SUBTASK_TYPE":
                if (params.length < 6) {
                    System.out.println("Ошибка: у сабтаска нет epicId -> " + value);
                    return null;
                }
                int epicId = Integer.parseInt(params[5]); // Получаем ID эпика, к которому относится подзадача
                Subtask subtask = new Subtask(name, description, status, epicId);
                subtask.setId(id);
                subtask.setTypeTask(TaskType.SUBTASK_TYPE);
                return subtask;

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }


    // Метод сохраняет текущее состояние менеджера (задачи, эпики, подзадачи) в файл.
    void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic"); // Заголовок файла

            // Сохраняем задачи
            for (Task task : tasks.values()) {
                //System.out.println("Сохранение задачи: " + toString(task));//  Лог
                writer.write("\n" + toString(task));
            }

            // Сохраняем эпики
            for (Epic epic : epics.values()) {
                //System.out.println("Сохранение эпика: " + toString(epic)); //  Лог
                writer.write("\n" + toString(epic));
            }

            // Сохраняем подзадачи
            for (Subtask subtask : subtasks.values()) {
                //System.out.println("Сохранение сабтаска: " + toString(subtask)); //  Лог
                writer.write("\n" + toString(subtask));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }


    // Переопределенные методы для добавления, удаления и обновления задач, эпиков и подзадач.
    // После каждого изменения вызывается метод save() для сохранения состояния в файл.
    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }


    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }


    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }


    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return super.getAllSubtask();
    }
}
