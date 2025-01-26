package manager;

import entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();
    private Node<Task> tail;
    private Node<Task> head;


    // Метод добавления задачи в конец списка
    private void linkLast (Task task) {
        Node<Task> newNode = new Node <>(task); // Создаем новый узел с данными задачи
        if (head == null) { // Если список пустой устанавливаем голову и хвост
            tail = newNode;
            head = newNode;
        } else { // Если список не пустой, добавляем новый узел в конец списка
            tail.next = newNode; // Устанавливаем ссылку "Next" хвоста на новый узел
            newNode.previous = tail; // Устанавливаем ссылку "Previous" нового узла на текущий хвост
            tail = newNode; // Обновляем хвост, чтобы он указывал на новый узел
        }
        nodeMap.put(task.getId(), newNode); // Добавляем узел в HashMap для быстрого доступа по id задачи
    }


    // Собираем все задачи в ArrayList
    private ArrayList<Task> getTasks() {
        ArrayList <Task> tasks = new ArrayList<>(); // Создаем пустой список, который будет хранить объекты типа Task
        Node<Task> curentNode = head; // Инициализируем переменную currentNode значением головы списка
        while (curentNode != null) { // Пока текущий узел не является null (конец списка)
            tasks.add(curentNode.data); // Добавляем данные текущего узла (задачу) в список tasks
            curentNode = curentNode.next; // Переходим к следующему узлу в списке
        }
        return tasks; // Возвращаем список
    }


    // Метод для добавления задачи в историю
    @Override
    public void add(Task task) {
        remove(task.getId()); // Удаляем предыдущий просмотр, если он был
        linkLast(task); // Добавляем задачу в конец списка
    }


    // Метод для удаления задачи из истории по id
    @Override
    public void remove(int id) {
        Node<Task> node = nodeMap.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }


    private void removeNode(Node<Task> node) {
        // 1. Перепривязывание предыдущего узла
        if (node.previous != null) { // Если узел не первый, перепривязываем предыдущий
            node.previous.next = node.next; // Обновляем ссылку "next" предыдущего узла, чтобы она указывала на
            // следующий узел текущего узла node.next
        // 2. Обновление головы
        } else { // Если узел первый, перемещаем голову
            head = node.next; // Обновляем голову, чтобы она указывала на следующий узел текущего узла node.next
        }

        // 3. Перепривязывание следующего узла
        if (node.next != null) { // Если узел не последний, перепривязываем следующий узел
            node.next.previous = node.previous; // Обновляем ссылку "previous" следующего узла "node next", чтобы она
            // указывала на предыдущий узел текущего узла node.previous
        } else { // Если узел последний, перемещаем хост
            tail = node.previous; // Обновляем хвост, чтобы он указывал на предыдущий узел текущего узла node.prev
        }
    }


    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }
}
