package entity;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

// Это класс для эпиков — задач, которые состоят из подзадач (Subtask)
public class Epic extends Task {

    private ArrayList<Integer> subtaskIdList; // Список ID подзадач, входящих в этот эпик

    private LocalDateTime endTime; // Новое поле: время завершения эпика, рассчитывается на основе подзадач


    // Конструктор для создания нового эпика
    public Epic(String name, String description) {
        super(name, description, Status.NEW); // Вызываем конструктор суперкласса Task
        this.subtaskIdList = new ArrayList<>(); // Создаём пустой список подзадач
        this.typeTask = TaskType.EPIC_TYPE; // Устанавливаем тип как EPIC
    }


    public ArrayList<Integer> getSubtaskIdList() { // Геттер для получения списка ID подзадач
        return subtaskIdList;
    }


    public void addSubtask(int subtaskId) { // Метод для добавления ID подзадачи в эпик
        subtaskIdList.add(subtaskId);
    }


    public void deleteSubtask(int subtaskId) { // Метод для удаления ID подзадачи из эпика
        subtaskIdList.remove((Integer) subtaskId); // Удаляем ID подзадачи из списка (приводим к Integer,
        // так как remove работает с объектами)
    }

    public TaskType getType() {
        return typeTask;
    }


    // Метод для расчёта времени и продолжительности эпика на основе подзадач
    public void updateTimeAndDuration(HashMap<Integer, Subtask> subtasks) {
        if (subtaskIdList.isEmpty()) { // Если подзадач нет
            setDuration(null); // Если нет подзадач, продолжительность null
            setStartTime(null); // Если нет подзадач, время начала null
            this.endTime = null; // Если нет подзадач, время завершения null
            return; // Выходим из метода
        }

        Duration totalDuration = Duration.ZERO; // Суммарная продолжительность всех подзадач, изначально 0
        LocalDateTime earliestStart = null; // Самое раннее время начала среди подзадач
        LocalDateTime latestEnd = null; // Самое позднее время завершения среди подзадач

        // Проходим по всем ID подзадач
        for (int subtaskId : subtaskIdList) {
            Subtask subtask = subtasks.get(subtaskId); // Получаем подзадачу по её ID из HashMap
            if (subtask != null) { // Если подзадача существует
                Duration subDuration = subtask.getDuration(); // Получаем продолжительность подзадачи
                LocalDateTime subStart = subtask.getStartTime(); // Получаем время начала подзадачи
                LocalDateTime subEnd = subtask.getEndTime(); // Получаем время окончания подзадачи

                if (subDuration != null) { // Если продолжительность подзадачи указана
                    totalDuration = totalDuration.plus(subDuration); // Суммируем продолжительность подзадач к общей
                    // продолжительности
                }
                if (subStart != null && (earliestStart == null || subStart.isBefore(earliestStart))) {
                    earliestStart = subStart; // Если это самое раннее время начала, обновляем earliestStart
                }
                if (subEnd != null && (latestEnd == null || subEnd.isAfter(latestEnd))) {
                    latestEnd = subEnd; // Если это самое позднее время окончания, обновляем latestEnd
                }
            }
        }

        setDuration(totalDuration); // Устанавливаем суммарную продолжительность эпика
        setStartTime(earliestStart); // Устанавливаем самое раннее время начала
        this.endTime = latestEnd; // Устанавливаем самое позднее время завершения
    }

    @Override  // Геттер для времени окончания эпика
    public LocalDateTime getEndTime() {
        return endTime; // Возвращаем рассчитанное время завершения
    }

    @Override // Переопределяем toString для вывода информации об эпике
    public String toString() { // переопределяем toString для чтения эпика
        return "Epic{" + "id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", status=" + getStatus() +
                ", subtaskIdList=" + subtaskIdList +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() : "null") + // Выводим duration в минутах
                ", startTime=" + getStartTime() + // Выводим время начала
                ", endTime=" + getEndTime() + // Выводим рассчитанное время завершения
                "}";
    }

}