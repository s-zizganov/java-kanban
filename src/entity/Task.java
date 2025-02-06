package entity;

import manager.TaskType;

import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private int id;
    private Status status;
    TaskType typeTask;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.typeTask = TaskType.TASK_TYPE;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getType() {
        return typeTask;
    }

    public void setTypeTask(TaskType typeTask) {
        this.typeTask = typeTask;
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", status=" + status +
                "}";
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // проверяем адреса объектов
        if (obj == null) return false; // проверяем ссылку на null
        if (this.getClass() != obj.getClass()) return false; // сравниваем классы объектов
        Task task = (Task) obj; // приводим второй объект к классу Task
        return id == task.id; // сравнение по id
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}