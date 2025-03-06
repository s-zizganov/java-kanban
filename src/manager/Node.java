package manager;

// Это класс узла для двусвязного списка, используемого в истории просмотров
public class Node<T> {
    T data; // Данные узла (в нашем случае — задача)
    Node<T> next; // Ссылка на следующий узел
    Node<T> previous; // Ссылка на предыдущий узел

    // Конструктор для создания узла с указанием всех полей
    public Node(T data, Node<T> previous, Node<T> next) {
        this.data = data;
        this.previous = previous;
        this.next = next;
    }

    // Конструктор для создания узла только с данными (ссылки null)
    public Node(T data) {
        this.data = data;
    }


}