package entity;

import java.util.ArrayList;



public class Epic extends Task {

    private ArrayList<Integer> subtaskIdList;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtaskIdList = new ArrayList<>();
    }


    public ArrayList<Integer> getSubtaskIdList() {// получение списка ID подзадач
        return subtaskIdList;
    }


    public void addSubtask(int subtaskId) { // метод добавления сабтаска в эпик
        subtaskIdList.add(subtaskId);
    }


    public void deleteSubtask(int subtaskId) {// метод для удаления сабтаска из эпика
        subtaskIdList.remove((Integer) subtaskId);
    }


    @Override
    public String toString() { // переопределяем toString для чтения эпика
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", status=" + getStatus() +
                ", subtaskIdList=" + subtaskIdList +
                "}";
    }
}
