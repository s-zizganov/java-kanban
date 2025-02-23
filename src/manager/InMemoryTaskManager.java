package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// Это класс для управления задачами, который хранит всё в памяти (не в файле)
public class InMemoryTaskManager implements TaskManager {

    int idCounter = 0; // Счётчик для генерации уникальных ID задач


    // Создадим хэшмапы для хранения всех видов задач
    protected final HashMap<Integer, Task> tasks = new HashMap<>(); // Хранилище обычных задач (ключ — ID, значение — задача)
    protected final HashMap<Integer, Epic> epics = new HashMap<>(); // Хранилище эпиков
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>(); // Хранилище подзадач
    protected final HistoryManager historyManager = Managers.getDefaultHistory(); // Объект для управления историей просмотров
    // Новое поле: TreeSet для хранения отсортированных задач и подзадач по startTime
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            LocalDateTime start1 = o1.getStartTime(); // Время начала первой задачи
            LocalDateTime start2 = o2.getStartTime(); // Время начала второй задачи
            // Сравниваем по startTime, если оба значения не null
            if (start1 == null && start2 == null) return 0; // Оба null, считаем равными (хотя они не попадут в TreeSet)
            if (start1 == null) return 1; // Задача без времени начала считается "позже"
            if (start2 == null) return -1; // Задача с временем начала "раньше"
            return start1.compareTo(start2); // Сравнение по времени начала
        }
    });

    // Метод для генерации нового уникального ID
    private int createId() {
        return ++idCounter;
    }


    // Реализация методов интерфейса:

    // Методы для работы с задачами Task:
    @Override
    public Task addTask(Task task) { // Метод для добавления обычной задачи

        // Проверяем пересечения с существующими задачами и подзадачами
        if (task.getStartTime() != null && task.getDuration() != null) {
            boolean hasOverlap = prioritizedTasks.stream()
                    .anyMatch(existingTask -> task.isOverlapping(existingTask)); // Проверяем все задачи в prioritizedTasks
            if (hasOverlap) {
                throw new IllegalArgumentException("Задачи пересекаются по времени");
            }
        }

        int id = createId(); // Генерируем новый ID
        task.setId(id); // Устанавливаем ID задаче
        tasks.put(id, task); // сохраняем задачу в хешмап

        // Добавляем задачу в prioritizedTasks, только если у нее есть startTime
        if (task.getStartTime() != null) { // Если у задачи есть время начала
            prioritizedTasks.add(task); // Добавляем её в отсортированный список
        }
        return task; // Возвращаем добавленную задачу
    }


    @Override // Метод для удаления задачи по ID
    public void deleteTask(int id) { // удаляем задачу
        Task task = tasks.remove(id); // Удаляем задачу из хранилища и получаем её
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.remove(task); // Если у задачи было время начала, удаляем из отсортированного списка
        }
    }


    @Override // Метод для получения задачи по ID
    public Task getTask(int id) {
        Task task = tasks.get(id); // Получаем задачу из хранилища
        if (task != null) {
            historyManager.add(task); // Добавляем задачу в историю просмотров
        }
        return task;
    }


    @Override // Метод для получения всех обычных задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values()); // Возвращаем список всех задач
    }


    @Override // Метод для очистки всех обычных задач
    public void clearTasks() {
        tasks.clear(); // Очищаем хранилище задач
        prioritizedTasks.clear(); // Очищаем отсортированный список
    }


    @Override // Метод для обновления задачи
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) { // Если задача с таким ID существует

            // Удаляем старую версию задачи из prioritizedTasks, если она там была
            Task oldTask = tasks.get(task.getId()); // Получаем старую версию задачи
            if (oldTask.getStartTime() != null) {
                prioritizedTasks.remove(oldTask); // Удаляем старую задачу из отсортированного списка
            }

            // Проверяем пересечения с другими задачами, исключая саму себя
            if (task.getStartTime() != null && task.getDuration() != null) {
                boolean hasOverlap = prioritizedTasks.stream()
                        .filter(o -> o.getId() != task.getId()) // Исключаем саму задачу из проверки
                        .anyMatch(excistingTask -> task.isOverlapping(excistingTask));
                if (hasOverlap) {
                    prioritizedTasks.add(oldTask); // Восстанавливаем старую задачу, если есть пересечение
                    throw new IllegalArgumentException("Обновленная задача пересекается по времени с другой задачей");
                }
            }

            // Обновляем задачу в хранилище
            tasks.put(task.getId(), task);

            // Добавляем обновленную задачу в prioritizedTasks, если есть startTime
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task); // Добавляем обновлённую задачу в отсортированный список
            }
        }
    }


    // МЕТОДЫ ДЛЯ РАБОТЫ С Epic:

    @Override// Метод для добавления эпика
    public void addEpic(Epic epic) {
        int id = createId(); // Генерируем новый ID
        epic.setId(id); // Устанавливаем ID эпику
        epics.put(id, epic); // Добавляем эпик в хранилище эпиков
        // !!! Эпики не добавляем в prioritizedTasks, так как их startTime рассчитывается из подзадач
    }


    @Override // Метод для удаления эпика по ID
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id); // Удаляем эпик и получаем его
        if (epic != null) {
            // Удаляем все связанные подзадачи
            epic.getSubtaskIdList().stream()
                    .forEach(subtaskId -> {
                        Subtask subtask = subtasks.remove(subtaskId);  // Удаляем подзадачу
                        if (subtask != null && subtask.getStartTime() != null) {
                            prioritizedTasks.remove(subtask);  // Удаляем подзадачу из отсортированного списка
                        }
                    });
        }
    }

    @Override // Метод для получения эпика по ID
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }


    @Override // Метод для получения всех эпиков
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());// Возвращаем список всех эпиков
    }


    @Override // Метод для очистки всех эпиков и подзадач
    public void clearEpics() {
        epics.clear(); // Очищаем хранилище эпиков
        subtasks.clear(); // Очищаем хранилище подзадач
        prioritizedTasks.removeIf(task -> task.getType() == TaskType.SUBTASK_TYPE); // Удаляем все подзадачи из отсортированного списка
    }


    @Override // Метод для обновления эпика
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) { // Если эпик с таким ID существует
            epics.put(epic.getId(), epic); // Обновляем эпик в хранилище
            // !!! Эпики не влияют на prioritizedTasks напрямую
        }
    }


    //МЕТОДЫ ДЛЯ РАБОТЫ С ПОДЗАДАЧАМИ Subtask:

    @Override // Метод для добавления подзадачи
    public void addSubtask(Subtask subtask) {
        // Проверяем пересечения с существующими задачами и подзадачами
        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
            boolean hasOverlap = prioritizedTasks.stream()
                    .anyMatch(existingTask -> subtask.isOverlapping(existingTask));
            if (hasOverlap) {
                throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей");
            }
        }

        int id = createId(); // Генерируем новый ID
        subtask.setId(id); // Устанавливаем ID подзадаче
        subtasks.put(id, subtask); // Добавляем подзадачу в хранилище
        Epic epic = epics.get(subtask.getEpicId()); // Получаем эпик, к которому привязана подзадача
        if (epic != null) { // Если эпик существует
            epic.addSubtask(id); // Добавляем ID подзадачи в эпик
            updateEpicStatus(epic); // Обновляем статус эпика
            epic.updateTimeAndDuration(subtasks); // Обновляем время и продолжительность эпика

            // Добавляем подзадачу в prioritizedTasks, если у нее есть startTime
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask); // Добавляем подзадачу в отсортированный список
            }

        }
    }


    @Override // Метод для удаления подзадачи по ID
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id); // Удаляем подзадачу и получаем её
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId()); // Получаем связанный эпик
            if (epic != null) {
                epic.deleteSubtask(id); // Удаляем ID подзадачи из эпика
                updateEpicStatus(epic); // Обновляем статус эпика
                epic.updateTimeAndDuration(subtasks); // Обновляем время и продолжительность эпика после удаления подзадачи
            }
            // Удаляем подзадачу из prioritizedTasks, если она там была
            if (subtask.getStartTime() != null) {
                prioritizedTasks.remove(subtask); // Удаляем подзадачу из отсортированного списка
            }
        }
    }


    @Override // Метод для получения подзадачи по ID
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id); // Получаем подзадачу из хранилища
        if (subtask != null) {
            historyManager.add(subtask); // Добавляем подзадачу в историю просмотров
        }
        return subtask;
    }


    // Переписан метод getSubtasksForEpic с использованием Stream API
    @Override  // Метод для получения всех подзадач эпика
    public ArrayList<Subtask> getSubtasksForEpic(int epicId) {
        Epic epic = epics.get(epicId); // Получаем эпик по ID
        if (epic == null) {
            return new ArrayList<>(); // Возвращаем пустой список, если эпика нет
        }
        // Преобразуем список ID подзадач в поток, получаем подзадачи из HashMap и собираем в список
        return epic.getSubtaskIdList().stream()
                .map(subtasks::get)// Получаем подзадачу по ID
                .filter(Objects::nonNull) // Фильтруем null, если подзадача была удалена
                .collect(Collectors.toCollection(ArrayList::new)); // Собираем в ArrayList
    }


    // Переписан метод updateEpicStatus с использованием Stream API
    @Override  // Метод для обновления статуса эпика на основе подзадач
    public void updateEpicStatus(Epic epic) {
        List<Integer> subtaskIdList = epic.getSubtaskIdList(); // Получаем список ID подзадач
        if (subtaskIdList.isEmpty()) { // Если подзадач нет
            epic.setStatus(Status.NEW); // Статус эпика — NEW
            return;  // Выходим из метода
        }

        // Проверяем статусы подзадач с помощью Stream API
        // Проверяем, все ли подзадачи завершены (DONE)
        boolean allDone = subtaskIdList.stream()
                .map(subtasks::get)// Получаем подзадачу по ID
                .allMatch(subtask -> subtask.getStatus() == Status.DONE);

        // Проверяем, есть ли подзадачи в процессе выполнения (IN_PROGRESS)
        boolean anyInProgress = subtaskIdList.stream()
                .map(subtasks::get)// Получаем подзадачу по ID
                .anyMatch(subtask -> subtask.getStatus() == Status.IN_PROGRESS);

        // Устанавливаем статус эпика в зависимости от подзадач
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }


    @Override // Метод для обновления подзадачи
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) { // Если подзадача с таким ID существует
            //Удаляем старую версию подзадачи из prioritizedTasks
            Subtask oldSubtask = subtasks.get(subtask.getId()); // Получаем старую версию подзадачи
            if (oldSubtask.getStartTime() != null) {
                prioritizedTasks.remove(oldSubtask); // Удаляем старую подзадачу из отсортированного списка
            }


            // Проверяем пересечения с другими задачами, исключая саму себя
            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                boolean hasOverlap = prioritizedTasks.stream()
                        .filter(t -> t.getId() != subtask.getId())
                        .anyMatch(existingTask -> subtask.isOverlapping(existingTask));
                if (hasOverlap) {
                    prioritizedTasks.add(oldSubtask); // Восстанавливаем старую подзадачу
                    throw new IllegalArgumentException("Обновленная подзадача пересекается по времени с другой задачей");
                }
            }

            // Обновляем подзадачу
            subtasks.put(subtask.getId(), subtask); // Обновляем подзадачу в хранилище
            Epic epic = epics.get(subtask.getEpicId()); // Получаем связанный эпик
            if (epic != null) {
                updateEpicStatus(epic); // Обновляем статус эпика
                epic.updateTimeAndDuration(subtasks); // Обновляем время и продолжительность эпика после обновления подзадачи
            }

            // Добавляем обновленную подзадачу в prioritizedTasks, если есть startTime
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask); // Добавляем обновлённую подзадачу в отсортированный список
            }

        }
    }


    @Override// Возвращает список задач и подзадач, отсортированных по startTime
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks); // Возвращаем копию отсортированного списка со сложностью O(n)
    }

    @Override // Метод для получения всех подзадач
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

}