package entity;

public class Task {

    private String name;
    private String description;
    private int id;
    private Status status;

    public Task(String name, String description,  Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }


    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public int getId() {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

}