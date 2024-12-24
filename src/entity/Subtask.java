package entity;

import java.util.Objects;

public class Subtask extends Task {

    private int epicId; // ИД эпика, к которой привязан сабтаск

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;

    }


    public int getEpicId() { // гет метод получения ИД эпика
        return epicId;
    }

    public void setEpicId(int epicId) { // сэт метод для создания ИД эпика
        this.epicId = epicId;
    }


    @Override
    public String toString() { //переопределяем toString для чтения сабтаска
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", status=" + getStatus() +
                ", epicID=" + epicId +
                "}";
    }



}
