import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int idCounter = 0; // числовое поле счетчик для генерации идентефикаторов

    // Создадим хэшмапы для хранения всех видов задач
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();


    public int createId() { // метод для создания ИД
        return ++idCounter;
    }


// Методы для работы с задачами Task:

    public void addTask(Task task) {
        int id = createId(); // создаем id
        task.setId(id); // назначаем id для задачи
        tasks.put(id, task); // сохраняем задачу в хешмап
    }


    public void deleteTask(int id) { // удаляем задачу
        tasks.remove(id);
    }


    public Task getTask(int id) { // получаем задачу
        return tasks.get(id);
    }


    public ArrayList<Task> getAllTasks() { // возвращаем все задачи
        return new ArrayList<>(tasks.values());
    }


    public void clearTasks() {//удаление всех задач
        tasks.clear();
    }




// Методы для работы с эпиками Epic:

    public void addEpic(Epic epic) {
        int id = createId(); // создаем ИД
        epic.setId(id); // назаначаем ИД для эпика
        epics.put(id, epic); // сохраняем эпик в хешмап
    }


    public void deleteEpic(int id) {
        Epic epic = epics.remove(id); // Удаляем эпик. Создаем переменную, чтобы дальше можно было удалить
        // подзадачи именно этого эпика
        if (epic != null) { // удаляем все задачи, которые были в этом эпике
            for (int idSubtasks : epic.getSubtaskIdList()) {
                subtasks.remove(idSubtasks);
            }
        }
    }


    public Epic getEpic(int id) {
        return epics.get(id); // Получаем эпик по ID
    }


    public ArrayList<Epic> getAllEpics() {// возвращаем все эпики
        return new ArrayList<>(epics.values());
    }


    public void clearEpics() { // удаляем все эпики и сабтаски
        epics.clear();
        subtasks.clear();
    }




//Методы для работы с сабтасками Subtask:

    public void addSubtask (Subtask subtask) {
        int id = createId();// создаем ИД
        subtask.setId(id); // назнчаем ИД для сабтаска
        subtasks.put(id, subtask); // сохраняем сабтаск в хешмап
        Epic epic = epics.get(subtask.getEpicId()); // получаем эпик куда принадлежит сабтаск
        if (epic != null) {
            epic.addSubtask(id); // добавляем сабтаск в эпик
            updateEpicStatus(epic); // Пересчитываем статус эпика
        }
    }


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


    public Subtask getSubtask(int id) {// получаем сабтаск по ИД
        return subtasks.get(id);
    }


    public ArrayList<Subtask> getSubtasksForEpic(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId); // Находим эпик
        if (epic != null) {
            for (int subtaskID :epic.getSubtaskIdList()) { // Добавляем все сабтаски эпика в список
                result.add(subtasks.get(subtaskID));
            }
        } return result;
    }


    public void updateEpicStatus (Epic epic) {
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






}