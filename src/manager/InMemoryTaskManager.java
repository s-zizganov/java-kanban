package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private int idCounter = 0; // числовое поле счетчик для генерации идентефикаторов

    // Создадим хэшмапы для хранения всех видов задач
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();


    private int createId() { // метод для создания ИД
        return ++idCounter;
    }


    // Реализация методов интерфейса:

    // Методы для работы с задачами Task:
    @Override
    public Task addTask(Task task) {
        int id = createId(); // создаем id
        task.setId(id); // назначаем id для задачи
        tasks.put(id, task); // сохраняем задачу в хешмап
        return task;
    }


    @Override
    public void deleteTask(int id) { // удаляем задачу
        tasks.remove(id);
    }


    @Override
    public Task getTask(int id) { // получаем задачу
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }


    @Override
    public ArrayList<Task> getAllTasks() { // возвращаем все задачи
        return new ArrayList<>(tasks.values());
    }


    @Override
    public void clearTasks() { //удаление всех задач
        tasks.clear();
    }


    //Обновление задачи
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }


    // Методы для работы с эпиками Epic:
    @Override
    public void addEpic(Epic epic) {
        int id = createId(); // создаем ИД
        epic.setId(id); // назаначаем ИД для эпика
        epics.put(id, epic); // сохраняем эпик в хешмап
    }


    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id); // Удаляем эпик. Создаем переменную, чтобы дальше можно было удалить
        // подзадачи именно этого эпика
        if (epic != null) { // удаляем все задачи, которые были в этом эпике
            for (int idSubtasks : epic.getSubtaskIdList()) {
                subtasks.remove(idSubtasks);
            }
        }
    }


    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }


    @Override
    public ArrayList<Epic> getAllEpics() { // возвращаем все эпики
        return new ArrayList<>(epics.values());
    }


    @Override
    public void clearEpics() { // удаляем все эпики и сабтаски
        epics.clear();
        subtasks.clear();
    }


    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }


    //Методы для работы с сабтасками Subtask:
    @Override
    public void addSubtask(Subtask subtask) {
        int id = createId();// создаем ИД
        subtask.setId(id); // назнчаем ИД для сабтаска
        subtasks.put(id, subtask); // сохраняем сабтаск в хешмап
        Epic epic = epics.get(subtask.getEpicId()); // получаем эпик куда принадлежит сабтаск
        if (epic != null) {
            epic.addSubtask(id); // добавляем сабтаск в эпик
            updateEpicStatus(epic); // Пересчитываем статус эпика
        }
    }


    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id); // удаляем сабтаск
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId()); // находим эпик этого сабтаска
            if (epic != null) {
                epic.deleteSubtask(id); // удаляем сабтаск из эпика
                updateEpicStatus(epic); // Пересчитываем статус эпика
            }
        }
    }


    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }


    @Override
    public ArrayList<Subtask> getSubtasksForEpic(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId); // Находим эпик
        if (epic != null) {
            for (int subtaskID : epic.getSubtaskIdList()) { // Добавляем все сабтаски эпика в список
                result.add(subtasks.get(subtaskID));
            }
        }
        return result;
    }


    @Override
    public void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIDList = epic.getSubtaskIdList(); // получаем ИД всех сабтасков
        if (subtaskIDList.isEmpty()) {
            epic.setStatus(Status.NEW);// если нет сабтаксов в эпике, то статус эпика - NEW
            return;
        }

        boolean allDone = true; // проверяем все ли задачи сделаны
        boolean inProgress = false; // есть ли задачи в процессе выполнения

        for (int subtaskId : subtaskIDList) {
            Status status = subtasks.get(subtaskId).getStatus();
            if (status != Status.DONE) {
                allDone = false; // проверяем выполнены ли сабтаски, если есть невыполненные, то false
            }
            if (status == Status.IN_PROGRESS) {
                inProgress = true; // есть ли сабтаски в процесее выолнения
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (inProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }

    }


    //Обновление подзадачи
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic); // Пересчитываем статус эпика
            }
        }
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

}