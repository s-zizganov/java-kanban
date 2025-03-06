package entity;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

// Это базовый класс для всех задач в системе (Task, Epic, Subtask)
public class Task {


    private String name; // Название задачи, например "Сходить в магазин"
    private String description; // Описание задачи, например "Купить молоко"
    private int id; // Уникальный номер задачи, генерируется автоматически
    private Status status; // Статус задачи (NEW, IN_PROGRESS, DONE)
    protected TaskType typeTask; // Тип задачи (TASK, EPIC, SUBTASK), защищённый, чтобы подклассы могли его менять
    private Duration duration; // Новое поле: продолжительность задачи в минутах
    private LocalDateTime startTime; // Новое поле: время начала задачи

    // Конструктор для создания задачи с указанием времени и продолжительности
    public Task(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name; // Устанавливаем название
        this.description = description; // Устанавливаем описание
        this.status = status; // Устанавливаем статус
        this.typeTask = TaskType.TASK_TYPE; // По умолчанию это обычная задача (TASK)
        this.duration = duration; // Новое: Инициализируем продолжительность
        this.startTime = startTime; // Новое: Инициализируем время начала
    }

    // Конструктор для создания задачи без дополнительных полей (для старого кода)
    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.typeTask = TaskType.TASK_TYPE;
    }

    // Геттер для названия задачи
    public String getName() {
        return name;
    }

    // Сеттер для изменения названия задачи
    public void setName(String name) {
        this.name = name;
    }

    // Геттер для описания задачи
    public String getDescription() {
        return description;
    }

    // Сеттер для изменения описания задачи
    public void setDescription(String description) {
        this.description = description;
    }

    // Геттер для ID задачи
    public int getId() {
        return id;
    }

    // Сеттер для установки ID задачи
    public void setId(int id) {
        this.id = id;
    }

    // Геттер для статуса задачи
    public Status getStatus() {
        return status;
    }

    // Сеттер для изменения статуса задачи
    public void setStatus(Status status) {
        this.status = status;
    }

    // Геттер для получения типа задачи (TASK, EPIC или SUBTASK)
    public TaskType getType() {
        return typeTask;
    }

    // Геттер для продолжительности
    public Duration getDuration() {
        return duration;
    }

    // Сеттер для продолжительности
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    // Геттер для времени начала
    public LocalDateTime getStartTime() {
        return startTime;
    }

    // Сеттер для времени начала
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // Метод для получения времени завершения задачи
    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration); // Рассчитываем время завершения как startTime + duration
        }
        return null; // Если время начала или продолжительность не заданы, возвращаем null
    }

    // Метод проверки пересечения двух задач по времени
    public boolean isOverlapping(Task other) {
        // Если у одной из задач нет startTime или duration, пересечения нет
        if (this.getStartTime() == null || this.getDuration() == null ||
                other.getStartTime() == null || other.getDuration() == null) {
            return false; // Нет пересечения
        }

        // Получаем времена начала и окончания обеих задач
        LocalDateTime start1 = this.getStartTime();
        LocalDateTime end1 = this.getEndTime();
        LocalDateTime start2 = other.getStartTime();
        LocalDateTime end2 = other.getEndTime();

        // Проверяем наложение отрезков: start1 <= end2 && start2 <= end1
        return start1.isBefore(end2) && start2.isBefore(end1);

    }


    @Override // Переопределяем метод toString для красивого вывода информации о задаче
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", status=" + status +
                ", duration=" + (duration != null ? duration.toMinutes() : "null") + // Выводим duration в минутах
                ", startTime=" + startTime + // Выводим время начала
                ", endTime=" + getEndTime() + // Выводим рассчитанное время завершения
                "}";
    }


    @Override // Переопределяем метод equals для сравнения задач по ID
    public boolean equals(Object obj) {
        if (this == obj) return true; // проверяем адреса объектов
        if (obj == null) return false; // проверяем ссылку на null
        if (this.getClass() != obj.getClass()) return false; // сравниваем классы объектов
        Task task = (Task) obj; // приводим второй объект к классу Task
        return id == task.id; // сравнение по id
    }


    @Override  // Переопределяем метод hashCode для корректной работы в коллекциях
    public int hashCode() {
        return Objects.hashCode(id);
    }
}