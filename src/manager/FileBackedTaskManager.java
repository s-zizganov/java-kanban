package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


// Это класс для управления задачами с сохранением данных в файл
public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file; // Файл, в который будут сохраняться данные

    public FileBackedTaskManager(File file) { // Конструктор для создания менеджера с указанием файла
        this.file = file;
    }

    // Статический метод для загрузки данных из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file); // Создаём новый менеджер
        try {
            List<String> lines = Files.readAllLines(file.toPath()); // Читаем все строки из файла
            if (lines.size() <= 1) { // Если только заголовок или файл пустой
                return manager; // Возвращаем пустой менеджер
            }

            // Пропускаем заголовок и загружаем задачи
            lines.stream()
                    .skip(1) // Пропускаем заголовок
                    .map(manager::fromString) // Преобразуем строку в задачу
                    .forEach(task -> { // В зависимости от типа добавляем задачу в соответствующее хранилище
                        switch (task.getType()) {
                            case TASK_TYPE:
                                manager.tasks.put(task.getId(), task);
                                if (task.getStartTime() != null) {
                                    manager.prioritizedTasks.add(task);
                                }
                                break;
                            case EPIC_TYPE:
                                manager.epics.put(task.getId(), (Epic) task);
                                break;
                            case SUBTASK_TYPE:
                                manager.subtasks.put(task.getId(), (Subtask) task);
                                if (task.getStartTime() != null) {
                                    manager.prioritizedTasks.add(task);
                                }
                                Epic epic = manager.epics.get(((Subtask) task).getEpicId());
                                if (epic != null) {
                                    epic.addSubtask(task.getId());
                                    epic.updateTimeAndDuration(manager.subtasks);
                                }
                                break;
                        }
                        // Обновляем счётчик ID, если загруженный ID больше текущего
                        if (task.getId() > manager.idCounter) {
                            manager.idCounter = task.getId();
                        }
                    });

            // Пересчитываем время и статус для всех эпиков после загрузки
            manager.epics.values().forEach(epic -> {
                epic.updateTimeAndDuration(manager.subtasks); // Обновляем время
                manager.updateEpicStatus(epic); // Обновляем статус
            });

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки из файла", e);
        }
        return manager; // Возвращаем загруженный менеджер
    }


    // Метод для преобразования задачи в строку формата CSV
    private String toString(Task task) { // Преобразуем продолжительность в минуты, если она есть, иначе пустая строка
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        // Преобразуем время начала в строку ISO, если оно есть, иначе пустая строка
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "";
        // Для подзадач добавляем ID эпика, для остальных — пустая строка
        String epicId = task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : "";

        // Форматируем строку CSV с разделителями-запятыми
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                duration, // Добавлено поле
                startTime, // Добавлено поле
                epicId // Рефакторинг
        );
    }

    // Метод для создания задачи из строки CSV
    public Task fromString(String value) {
        String[] params = value.split(",", 8); // Разделяем строку на части (до 8 полей)

        int id = Integer.parseInt(params[0]);
        String type = params[1];
        String name = params[2];
        Status status = Status.valueOf(params[3]);
        String description = params[4];
        Duration duration = params[5].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(params[5])); // Преобразуем
        // продолжительность из строки в Duration, если она есть
        LocalDateTime startTime = params[6].isEmpty() ? null : LocalDateTime.parse(params[6]); // Преобразуем
        // время начала из строки в LocalDateTime, если оно есть

        // В зависимости от типа создаём нужный объект
        switch (type) {
            case "EPIC_TYPE":
                Epic epic = new Epic(name, description);
                epic.setId(Integer.parseInt(params[0]));
                epic.setStatus(Status.valueOf(params[3]));
                epic.getType(); // Устанавливаем тип
                epic.setDuration(duration); // Устанавливаем расчетные поля
                epic.setStartTime(startTime);
                return epic;

            case "TASK_TYPE":
                Task task = new Task(name, description, status, duration, startTime);
                task.setId(id);
                task.getType();
                return task;

            case "SUBTASK_TYPE":
                if (params.length < 8) {
                    System.out.println("Ошибка: у сабтаска нет epicId -> " + value);
                    return null;
                }
                int epicId = Integer.parseInt(params[7]); // Получаем ID эпика, к которому относится подзадача
                Subtask subtask = new Subtask(name, description, status, epicId, duration, startTime);
                subtask.setId(id);
                subtask.getType();
                return subtask;

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }


    // Сохранение состояния в файл
    private void save() {
        try (Writer writer = new FileWriter(file)) { // Открываем файл для записи
            // Заголовок CSV
            writer.write("id,type,name,description,status,duration,startTime,epicId\n");

            // Сохраняем все обычные задачи в файл
            getAllTasks().stream()
                    .map(this::toString)// Преобразуем задачу в строку CSV
                    .forEach(line -> {
                        try {
                            writer.write(line + "\n"); // Записываем строку в файл
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка записи задачи в файл", e);
                        }
                    });

            // Сохраняем все эпики в файл
            getAllEpics().stream()
                    .map(this::toString) // Преобразуем эпик в строку CSV
                    .forEach(line -> {
                        try {
                            writer.write(line + "\n"); // Записываем строку в файл
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка записи эпика в файл", e);
                        }
                    });

            // Сохраняем все подзадачи в файл
            getAllSubtask().stream()
                    .map(this::toString)// Преобразуем подзадачу в строку CSV
                    .forEach(line -> {
                        try {
                            writer.write(line + "\n"); // Записываем строку в файл
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка записи подзадачи в файл", e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения в файл", e);
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
