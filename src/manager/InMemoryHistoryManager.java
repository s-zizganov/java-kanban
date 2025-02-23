package manager;

import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Это класс для управления историей просмотров задач в памяти
public class InMemoryHistoryManager implements HistoryManager {
    // Хранилище узлов (Node) с задачами, ключ — ID задачи
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();
    private Node<Task> tail; // Последний узел в списке истории (хвост)
    private Node<Task> head; // Первый узел в списке истории (голова)


    // Метод добавления задачи в конец списка истории
    private void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task); // Создаем новый узел с данными задачи
        if (head == null) { // Если список пустой устанавливаем голову и хвост
            tail = newNode; // Новый узел становится и хвостом
            head = newNode; // и головой
        } else { // Если список не пустой
            tail.next = newNode; // Связываем текущий хвост с новым узлом
            newNode.previous = tail; // Устанавливаем ссылку "назад" нового узла на текущий хвост
            tail = newNode; // Новый узел становится новым хвостом
        }
        nodeMap.put(task.getId(), newNode); // Добавляем узел в HashMap для быстрого доступа по id задачи
    }


    // Метод для получения всех задач из истории в виде списка
    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>(); // Создаём пустой список для хранения задач
        Node<Task> curentNode = head; // Начинаем с головы списка
        while (curentNode != null) { // Пока не дойдём до конца списка
            tasks.add(curentNode.data); // Добавляем задачу из текущего узла в список
            curentNode = curentNode.next; // Переходим к следующему узлу в списке
        }
        return tasks; // Возвращаем список задач
    }


    @Override // Метод для добавления задачи в историю
    public void add(Task task) {
        remove(task.getId()); // Удаляем задачу, если она уже была в истории
        linkLast(task); // Добавляем задачу в конец списка
    }


    @Override // Метод для удаления задачи из истории по ID
    public void remove(int id) {
        Node<Task> node = nodeMap.remove(id); // Удаляем узел из HashMap и получаем его
        if (node == null) {
            return; // Если узла не было, ничего не делаем
        }
        removeNode(node); // Удаляем узел из списка
    }

    // Вспомогательный метод для удаления узла из двусвязного списка
    private void removeNode(Node<Task> node) {
        // 1. Перепривязывание предыдущего узла
        if (node.previous != null) { // Если узел не первый, обновляем ссылку "next" предыдущего узла
            node.previous.next = node.next; // Предыдущий узел теперь указывает на следующий
            // 2. Обновление головы
        } else { // Если узел первый, перемещаем голову
            head = node.next; // Если узел был головой, голова перемещается на следующий узел
        }

        // 3. Перепривязывание следующего узла
        if (node.next != null) {// Если узел не последний, обновляем ссылку "previous" следующего узла
            node.next.previous = node.previous; // Следующий узел теперь указывает на предыдущий
        } else { // Если узел последний, перемещаем хост
            tail = node.previous; // Если узел был хвостом, хвост перемещается на предыдущий узел
        }
    }


    @Override // Метод для получения всей истории просмотров
    public ArrayList<Task> getHistory() {
        return getTasks();
    }
}