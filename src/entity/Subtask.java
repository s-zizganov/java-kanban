package entity;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;


public class Subtask extends Task {

    private int epicId; // ИД эпика, к которой привязан сабтаск
    // private TaskType typeTask;

    // Новый конструктор с duration и startTime
    public Subtask(String name, String description, Status status, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
        this.typeTask = TaskType.SUBTASK_TYPE;
    }

    // Конструктор без новых полей
    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
        this.typeTask = TaskType.SUBTASK_TYPE;
    }


    public int getEpicId() {  // Геттер для получения ID эпика
        return epicId;
    }

    public void setEpicId(int epicId) { // сэт метод для создания ИД эпика
        this.epicId = epicId;
    }

    public TaskType getType() {
        return typeTask;
    }


    @Override
    public String toString() { //переопределяем toString для чтения сабтаска
        return "Subtask{" + "id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() +
                "'" + ", status=" + getStatus() +
                ", epicID=" + epicId +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() : "null") + // Выводим duration в минутах
                ", startTime=" + getStartTime() + // Выводим время начала
                ", endTime=" + getEndTime() + // Выводим рассчитанное время завершения
                "}";
    }
}